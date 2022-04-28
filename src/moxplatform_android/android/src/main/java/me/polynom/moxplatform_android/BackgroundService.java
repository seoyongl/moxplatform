package me.polynom.moxplatform_android;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.AlarmManagerCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import io.flutter.FlutterInjector;
import io.flutter.app.FlutterApplication;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.plugin.common.JSONMethodCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterCallbackInformation;
import io.flutter.view.FlutterMain;

public class BackgroundService extends Service implements MethodChannel.MethodCallHandler {
    private static String TAG = "BackgroundService";
    private static final String manuallyStoppedKey = "manually_stopped";
    private static final String backgroundMethodChannelKey = MoxplatformAndroidPlugin.methodChannelKey + "_bg";
    /// The [FlutterEngine] executing the background service
    private FlutterEngine engine;
    private MethodChannel methodChannel;
    private DartExecutor.DartCallback dartCallback;
    /// True if the service has been stopped by hand
    private boolean isManuallyStopped = false;
    /// Indicate if we're running
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private static final String WAKE_LOCK_NAME = BackgroundService.class.getName() + ".Lock";
    private static volatile PowerManager.WakeLock wakeLock = null;
    /// Notification data
    private String notificationBody = "Preparing...";
    private static final String notificationTitle = "Moxxy";

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (wakeLock == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            wakeLock = mgr.newWakeLock(PowerManager.FULL_WAKE_LOCK, WAKE_LOCK_NAME);
            wakeLock.setReferenceCounted(true);
        }
        return (wakeLock);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void enqueue(Context context) {
        Intent intent = new Intent(context, WatchdogReceiver.class);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        boolean aboveS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
        PendingIntent pending = PendingIntent.getBroadcast(
                context,
                111,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        // Only enable FLAG_MUTABLE when the Android version is Android S or greater
                        | (PendingIntent.FLAG_MUTABLE & (aboveS ? PendingIntent.FLAG_MUTABLE : 0))
        );
        AlarmManagerCompat.setAndAllowWhileIdle(manager, AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pending);
    }

    public static boolean isManuallyStopped(Context context) {
        return context.getSharedPreferences(MoxplatformAndroidPlugin.sharedPrefKey, MODE_PRIVATE).getBoolean(manuallyStoppedKey, false);
    }
    public void setManuallyStopped(Context context, boolean value) {
        context.getSharedPreferences(MoxplatformAndroidPlugin.sharedPrefKey, MODE_PRIVATE)
                .edit()
                .putBoolean(manuallyStoppedKey, value)
                .apply();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Moxxy Background Service";
            String description = "Executing Moxxy in the background";

            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("FOREGROUND_DEFAULT", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected void updateNotificationInfo() {
        String packageName = getApplicationContext().getPackageName();
        Intent i = getPackageManager().getLaunchIntentForPackage(packageName);

        boolean aboveS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
        PendingIntent pending = PendingIntent.getActivity(
                BackgroundService.this,
                99778,
                i,
                PendingIntent.FLAG_CANCEL_CURRENT
                        // Only enable on Android S or greater
                        | (PendingIntent.FLAG_MUTABLE & (aboveS ? PendingIntent.FLAG_MUTABLE : 0))
        );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "FOREGROUND_DEFAULT")
                .setSmallIcon(R.drawable.ic_service_icon)
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setContentIntent(pending);

        startForeground(99778, mBuilder.build());
    }

    private void runService() {
        try {
            if (isRunning.get() || (engine != null && !engine.getDartExecutor().isExecutingDart())) return;

            updateNotificationInfo();

            // Initialize Flutter if it's not already
            if (!FlutterInjector.instance().flutterLoader().initialized()) {
                FlutterInjector.instance().flutterLoader().startInitialization(getApplicationContext());
            }

            long entrypointHandle = getSharedPreferences(MoxplatformAndroidPlugin.sharedPrefKey, MODE_PRIVATE)
                    .getLong(MoxplatformAndroidPlugin.entrypointKey, 0);
            FlutterInjector.instance().flutterLoader().ensureInitializationComplete(getApplicationContext(), null);
            FlutterCallbackInformation callback = FlutterCallbackInformation.lookupCallbackInformation(entrypointHandle);
            if (callback == null) {
                Log.e(TAG, "Callback handle not found");
                return;
            }

            isRunning.set(true);
            engine = new FlutterEngine(this);
            engine.getServiceControlSurface().attachToService(BackgroundService.this, null, true);

            methodChannel = new MethodChannel(engine.getDartExecutor().getBinaryMessenger(), backgroundMethodChannelKey);
            methodChannel.setMethodCallHandler(this);
            Log.d(TAG, "Method channel is set up");

            dartCallback = new DartExecutor.DartCallback(getAssets(), FlutterInjector.instance().flutterLoader().findAppBundlePath(), callback);
            engine.getDartExecutor().executeDartCallback(dartCallback);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }

    public void receiveData(String data) {
        if (methodChannel != null) {
            methodChannel.invokeMethod(MoxplatformAndroidPlugin.dataReceivedMethodName, data);
        }
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "getHandler":
                result.success(MoxplatformAndroidPlugin.getHandle(this));
                break;
            case "getExtraData":
                result.success(MoxplatformAndroidPlugin.getExtraData(this));
                break;
            case "setNotificationBody":
                String body = ((String) ((ArrayList) call.arguments).get(0));
                notificationBody = body;
                updateNotificationInfo();
                result.success(true);
                break;
            case "sendData":
                LocalBroadcastManager sendDataManager = LocalBroadcastManager.getInstance(this);
                Intent sendDataIntent = new Intent(MoxplatformAndroidPlugin.methodChannelKey);
                sendDataIntent.putExtra("data", (String) call.arguments);
                sendDataManager.sendBroadcast(sendDataIntent);
                result.success(true);
                break;
            case "stop":
                isManuallyStopped = true;
                Intent stopIntent = new Intent(this, WatchdogReceiver.class);
                boolean aboveS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
                PendingIntent pending = PendingIntent.getBroadcast(
                        getApplicationContext(),
                        111,
                        stopIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                                // Only enable FLAG_MUTABLE when the Android version is Android S or greater
                                | (PendingIntent.FLAG_MUTABLE & (aboveS ? PendingIntent.FLAG_MUTABLE : 0)));
                AlarmManager stopManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                stopManager.cancel(pending);
                stopSelf();
                MoxplatformAndroidPlugin.setStartAtBoot(this, false);
                result.success(true);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        notificationBody = "Preparing...";
        updateNotificationInfo();
    }

    @Override
    public void onDestroy() {
        if (!isManuallyStopped) {
            enqueue(this);
        } else {
            setManuallyStopped(this,true);
        }

        if (engine != null) {
            engine.getServiceControlSurface().detachFromService();
            engine.destroy();
            engine = null;
        }

        stopForeground(true);
        isRunning.set(false);

        methodChannel = null;
        dartCallback = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setManuallyStopped(this,false);
        enqueue(this);
        runService();
        getLock(getApplicationContext()).acquire();

        return START_STICKY;
    }
}

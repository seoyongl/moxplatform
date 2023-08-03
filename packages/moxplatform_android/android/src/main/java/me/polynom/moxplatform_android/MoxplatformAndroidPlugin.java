package me.polynom.moxplatform_android;

import static androidx.core.content.ContextCompat.getSystemService;
import static me.polynom.moxplatform_android.ConstantsKt.SHARED_PREFERENCES_KEY;
import static me.polynom.moxplatform_android.CryptoKt.*;
import static me.polynom.moxplatform_android.RecordSentMessageKt.*;

import me.polynom.moxplatform_android.Api.*;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.service.ServiceAware;
import io.flutter.embedding.engine.plugins.service.ServicePluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.JSONMethodCodec;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MoxplatformAndroidPlugin extends BroadcastReceiver implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ServiceAware, MoxplatformApi {
    public static final String entrypointKey = "entrypoint_handle";
    public static final String extraDataKey = "extra_data";
    private static final String autoStartAtBootKey = "auto_start_at_boot";
    private static final String TAG = "moxplatform_android";
    public static final String methodChannelKey = "me.polynom.moxplatform_android";
    public static final String dataReceivedMethodName = "dataReceived";

    private static final List<MoxplatformAndroidPlugin> _instances = new ArrayList<>();
    private BackgroundService service;
    private MethodChannel channel;
    private static EventChannel notificationChannel;
    public static EventSink notificationSink;

    private Context context;

    public MoxplatformAndroidPlugin() {
        _instances.add(this);
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), methodChannelKey);
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();

        notificationChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "me.polynom/notification_stream");
        notificationChannel.setStreamHandler(this);


        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
        localBroadcastManager.registerReceiver(this, new IntentFilter(methodChannelKey));

        MoxplatformApi.setup(flutterPluginBinding.getBinaryMessenger(), this);

        Log.d(TAG, "Attached to engine");
    }

    static void registerWith(Registrar registrar) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(registrar.context());
        final MoxplatformAndroidPlugin plugin = new MoxplatformAndroidPlugin();
        localBroadcastManager.registerReceiver(plugin, new IntentFilter(methodChannelKey));

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "me.polynom/background_service_android", JSONMethodCodec.INSTANCE);
        channel.setMethodCallHandler(plugin);
        plugin.channel = channel;

        Log.d(TAG, "Registered against registrar");
    }

    @Override
    public void onCancel(Object arguments) {
        Log.d(TAG, "Removed listener");
        notificationSink = null;
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink eventSink) {
        Log.d(TAG, "Attached listener");
        notificationSink = eventSink;
    }

    /// Store the entrypoint handle and extra data for the background service.
    private void configure(long entrypointHandle, String extraData) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        prefs.edit().putLong(entrypointKey, entrypointHandle).putString(extraDataKey, extraData).apply();
    }

    public static long getHandle(Context c) {
        return c.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).getLong(entrypointKey, 0);
    }

    public static String getExtraData(Context c) {
        return c.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).getString(extraDataKey, "");
    }

    public static void setStartAtBoot(Context c, boolean value) {
        c.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).edit().putBoolean(autoStartAtBootKey, value).apply();
    }

    public static boolean getStartAtBoot(Context c) {
        return c.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).getBoolean(autoStartAtBootKey, false);
    }

    private boolean isRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BackgroundService.class.getName().equals(info.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull io.flutter.plugin.common.MethodChannel.Result result) {
        switch (call.method) {
            case "configure":
                ArrayList args = (ArrayList) call.arguments;
                long handle = (long) args.get(0);
                String extraData = (String) args.get(1);

                configure(handle, extraData);
                result.success(true);
                break;
            case "isRunning":
                result.success(isRunning());
                break;
            case "start":
                MoxplatformAndroidPlugin.setStartAtBoot(context, true);
                BackgroundService.enqueue(context);
                Intent intent = new Intent(context, BackgroundService.class);
                ContextCompat.startForegroundService(context, intent);
                Log.d(TAG, "Service started");
                result.success(true);
                break;
            case "sendData":
                for (MoxplatformAndroidPlugin plugin : _instances) {
                    if (plugin.service != null) {
                        plugin.service.receiveData((String) call.arguments);
                        break;
                    }
                }
                result.success(true);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;

        if (intent.getAction().equalsIgnoreCase(methodChannelKey)) {
            String data = intent.getStringExtra("data");

            if (channel != null) {
                channel.invokeMethod(dataReceivedMethodName, data);
            }
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
        localBroadcastManager.unregisterReceiver(this);

        Log.d(TAG, "Detached from engine");
    }

    @Override
    public void onAttachedToService(@NonNull ServicePluginBinding binding) {
        Log.d(TAG, "Attached to service");
        this.service = (BackgroundService) binding.getService();
    }

    @Override
    public void onDetachedFromService() {
        Log.d(TAG, "Detached from service");
        this.service = null;
    }

    @Override
    public void createNotificationChannel(@NonNull String title, @NonNull String description, @NonNull String id, @NonNull Boolean urgent) {
        final NotificationChannel channel = new NotificationChannel(id, title, urgent ? NotificationManager.IMPORTANCE_HIGH : NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableVibration(true);
        channel.enableLights(true);
        channel.setDescription(description);
        final NotificationManager manager = getSystemService(context, NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Override
    public void showMessagingNotification(@NonNull MessagingNotification notification) {
        NotificationsKt.showMessagingNotification(context, notification);
    }

    @Override
    public void showNotification(@NonNull RegularNotification notification) {
        NotificationsKt.showNotification(context, notification);
    }

    @Override
    public void dismissNotification(@NonNull Long id) {
        NotificationManagerCompat.from(context).cancel(id.intValue());
    }

    @Override
    public void setNotificationSelfAvatar(@NonNull String path) {
        NotificationDataManager.INSTANCE.setAvatarPath(context, path);
    }

    @Override
    public void setNotificationI18n(@NonNull NotificationI18nData data) {
        // Configure i18n
        NotificationDataManager.INSTANCE.setYou(context, data.getYou());
        NotificationDataManager.INSTANCE.setReply(context, data.getReply());
        NotificationDataManager.INSTANCE.setMarkAsRead(context, data.getMarkAsRead());
    }

    @NonNull
    @Override
    public String getPersistentDataPath() {
        return context.getFilesDir().getPath();
    }

    @NonNull
    @Override
    public String getCacheDataPath() {
        return context.getCacheDir().getPath();
    }

    @Override
    public void recordSentMessage(@NonNull String name, @NonNull String jid, @Nullable String avatarPath, @NonNull FallbackIconType fallbackIcon) {
        systemRecordSentMessage(context, name, jid, avatarPath, fallbackIcon);
    }

    @Override
    public void encryptFile(@NonNull String sourcePath, @NonNull String destPath, @NonNull byte[] key, @NonNull byte[] iv, @NonNull CipherAlgorithm algorithm, @NonNull String hashSpec, @NonNull Api.Result<CryptographyResult> result) {
        CryptoKt.encryptAndHash(
                sourcePath,
                destPath,
                key,
                iv,
                algorithm,
                hashSpec,
                result
        );
    }

    @Override
    public void decryptFile(@NonNull String sourcePath, @NonNull String destPath, @NonNull byte[] key, @NonNull byte[] iv, @NonNull CipherAlgorithm algorithm, @NonNull String hashSpec, @NonNull Api.Result<CryptographyResult> result) {
        CryptoKt.decryptAndHash(
                sourcePath,
                destPath,
                key,
                iv,
                algorithm,
                hashSpec,
                result
        );
    }

    @Override
    public void hashFile(@NonNull String sourcePath, @NonNull String hashSpec, @NonNull Api.Result<byte[]> result) {
        CryptoKt.hashFile(sourcePath, hashSpec, result);
    }

    @Override
    public void eventStub(@NonNull NotificationEvent event) {
        // Stub to trick pigeon into
    }
}

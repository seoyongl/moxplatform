package me.polynom.moxplatform_android;

import static androidx.core.content.ContextCompat.getSystemService;
import static me.polynom.moxplatform_android.RecordSentMessageKt.recordSentMessage;
import static me.polynom.moxplatform_android.CryptoKt.*;
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
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.service.ServiceAware;
import io.flutter.embedding.engine.plugins.service.ServicePluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.JSONMethodCodec;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MoxplatformAndroidPlugin extends BroadcastReceiver implements FlutterPlugin, MethodCallHandler, ServiceAware, MoxplatformApi {
  public static final String entrypointKey = "entrypoint_handle";
  public static final String extraDataKey = "extra_data";
  private static final String autoStartAtBootKey = "auto_start_at_boot";
  public static final String sharedPrefKey = "me.polynom.moxplatform_android";
  private static final String TAG = "moxplatform_android";
  public static final String methodChannelKey = "me.polynom.moxplatform_android";
  public static final String dataReceivedMethodName = "dataReceived";

  private static final List<MoxplatformAndroidPlugin> _instances = new ArrayList<>();
  private BackgroundService service;
  private MethodChannel channel;
  private Context context;

  public MoxplatformAndroidPlugin() {
    _instances.add(this);
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), methodChannelKey);
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();

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

  /// Store the entrypoint handle and extra data for the background service.
  private void configure(long entrypointHandle, String extraData) {
    SharedPreferences prefs = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE);
    prefs.edit()
            .putLong(entrypointKey, entrypointHandle)
            .putString(extraDataKey, extraData)
            .apply();
  }

  public static long getHandle(Context c) {
    return c.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).getLong(entrypointKey, 0);
  }

  public static String getExtraData(Context c) {
    return c.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).getString(extraDataKey, "");
  }

  public static void setStartAtBoot(Context c, boolean value) {
    c.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(autoStartAtBootKey, value)
            .apply();
  }
  public static boolean getStartAtBoot(Context c) {
    return c.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).getBoolean(autoStartAtBootKey, false);
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
      case "encryptFile":
        Thread encryptionThread = new Thread(new Runnable() {
          @Override
          public void run() {
            ArrayList args = (ArrayList) call.arguments;
            String src = (String) args.get(0);
            String dest = (String) args.get(1);
            byte[] key = (byte[]) args.get(2);
            byte[] iv = (byte[]) args.get(3);
            int algorithm = (int) args.get(4);
            String hashSpec = (String) args.get(5);

            result.success(
                    encryptAndHash(
                            src,
                            dest,
                            key,
                            iv,
                            getCipherSpecFromInteger(algorithm),
                            hashSpec
                    )
            );
          }
        });
        encryptionThread.start();
        break;
      case "decryptFile":
        Thread decryptionThread = new Thread(new Runnable() {
          @Override
          public void run() {
            ArrayList args = (ArrayList) call.arguments;
            String src = (String) args.get(0);
            String dest = (String) args.get(1);
            byte[] key = (byte[]) args.get(2);
            byte[] iv = (byte[]) args.get(3);
            int algorithm = (int) args.get(4);
            String hashSpec = (String) args.get(5);

            result.success(
                    decryptAndHash(
                            src,
                            dest,
                            key,
                            iv,
                            getCipherSpecFromInteger(algorithm),
                            hashSpec
                    )
            );
          }
        });
        decryptionThread.start();
        break;
      case "hashFile":
        Thread hashingThread = new Thread(new Runnable() {
          @Override
          public void run() {
            ArrayList args = (ArrayList) call.arguments;
            String src = (String) args.get(0);
            String hashSpec = (String) args.get(1);

            result.success(hashFile(src, hashSpec));
          }
        });
        hashingThread.start();
        break;
      case "recordSentMessage":
        ArrayList rargs = (ArrayList) call.arguments;
        recordSentMessage(
                context,
                (String) rargs.get(0),
                (String) rargs.get(1),
                (String) rargs.get(2),
                (int) rargs.get(3)
        );
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
  public void createNotificationChannel(@NonNull String title, @NonNull String id, @NonNull Boolean urgent) {
      final NotificationChannel channel = new NotificationChannel(
              id,
              title,
              urgent ? NotificationManager.IMPORTANCE_HIGH : NotificationManager.IMPORTANCE_DEFAULT
      );
      channel.enableVibration(true);
      channel.enableLights(true);
      final NotificationManager manager = getSystemService(context, NotificationManager.class);
      manager.createNotificationChannel(channel);
  }

  @Override
  public void showMessagingNotification(@NonNull MessagingNotification notification) {
    NotificationsKt.showMessagingNotification(context, notification);
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
}

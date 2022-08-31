package me.polynom.moxplatform_android;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {
    private final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MoxplatformAndroidPlugin.getStartAtBoot(context)) {
	    if (BackgroundService.wakeLock == null) {
		Log.d(TAG, "Wakelock is null. Acquiring it...");
		BackgroundService.getLock(context).acquire(MoxplatformConstants.WAKE_LOCK_DURATION);
		Log.d(TAG, "Wakelock acquired...");
	    }

            ContextCompat.startForegroundService(context, new Intent(context, BackgroundService.class));
        }
    }
}

package me.polynom.moxplatform_android

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // If it is a mark as read, dismiss the entire notification and
        // send a notification to the app.
        // TODO: Notify app
        if (intent.action == MARK_AS_READ_ACTION) {
            Log.d("NotificationReceiver", "Marking ${intent.getStringExtra("title")} as read")
            NotificationManagerCompat.from(context).cancel(intent.getLongExtra(MARK_AS_READ_ID_KEY, -1).toInt())
            return
        }

        val remoteInput = RemoteInput.getResultsFromIntent(intent) ?: return

        val title = remoteInput.getCharSequence(REPLY_TEXT_KEY).toString()
        Log.d("NotificationReceiver", title)
        // TODO: Notify app
    }
}
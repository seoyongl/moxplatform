package me.polynom.moxplatform_android

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import me.polynom.moxplatform_android.Api.NotificationEvent

class NotificationReceiver : BroadcastReceiver() {
    private fun handleMarkAsRead(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "Marking ${intent.getStringExtra("jid")} as read")
        val jidWrapper = intent.getStringExtra("jid") ?: ""
        NotificationManagerCompat.from(context).cancel(intent.getLongExtra(MARK_AS_READ_ID_KEY, -1).toInt())
        MoxplatformAndroidPlugin.notificationSink?.success(
            NotificationEvent().apply {
                // TODO: Use constant for key
                // TODO: Fix
                jid = jidWrapper
                type = Api.NotificationEventType.MARK_AS_READ
                payload = null
            }.toList()
        )

        // Dismiss the notification
        val notificationId = intent.getLongExtra("notification_id", -1).toInt()
        if (notificationId != -1) {
            NotificationManagerCompat.from(context).cancel(
                notificationId,
            )
        } else {
            Log.e("NotificationReceiver", "No id specified. Cannot dismiss notification")
        }
    }

    private fun handleReply(context: Context, intent: Intent) {
        val jidWrapper = intent.getStringExtra("jid") ?: ""
        val remoteInput = RemoteInput.getResultsFromIntent(intent) ?: return
        Log.d("NotificationReceiver", "Got a reply for ${jidWrapper}")
        // TODO: Notify app
        MoxplatformAndroidPlugin.notificationSink?.success(
            NotificationEvent().apply {
                // TODO: Use constant for key
                jid = jidWrapper
                type = Api.NotificationEventType.REPLY
                payload = remoteInput.getCharSequence(REPLY_TEXT_KEY).toString()
            }.toList()
        )

        // TODO: Update the notification to prevent showing the spinner
    }

    override fun onReceive(context: Context, intent: Intent) {
        // TODO: We need to be careful to ensure that the Flutter engine is running.
        //       If it's not, we have to start it. However, that's only an issue when we expect to
        //       receive notifications while not running, i.e. Push Notifications.
        when (intent.action) {
            MARK_AS_READ_ACTION -> handleMarkAsRead(context, intent)
            REPLY_ACTION -> handleReply(context, intent)
            // TODO: Handle tap
        }
    }
}
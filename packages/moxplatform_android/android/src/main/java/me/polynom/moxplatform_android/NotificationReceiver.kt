package me.polynom.moxplatform_android

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.IconCompat
import me.polynom.moxplatform_android.Api.NotificationEvent
import java.io.File
import java.time.Instant

class NotificationReceiver : BroadcastReceiver() {
    /*
     * Dismisses the notification through which we received @intent.
     * */
    private fun dismissNotification(context: Context, intent: Intent) {
        // Dismiss the notification
        val notificationId = intent.getLongExtra(NOTIFICATION_EXTRA_ID_KEY, -1).toInt()
        if (notificationId != -1) {
            NotificationManagerCompat.from(context).cancel(
                notificationId,
            )
        } else {
            Log.e("NotificationReceiver", "No id specified. Cannot dismiss notification")
        }
    }

    private fun findActiveNotification(context: Context, id: Int): Notification? {
        return (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .activeNotifications
            .find { it.id == id }?.notification
    }

    private fun extractPayloadMapFromIntent(intent: Intent): Map<String?, String?> {
        val extras = mutableMapOf<String?, String?>()
        intent.extras?.keySet()!!.forEach {
            Log.d(TAG, "Checking $it -> ${intent.extras!!.get(it)}")
            if (it.startsWith("payload_")) {
                Log.d(TAG, "Adding $it")
                extras[it.substring(8)] = intent.extras!!.getString(it)
            }
        }

        return extras
    }

    private fun handleMarkAsRead(context: Context, intent: Intent) {
        MoxplatformAndroidPlugin.notificationSink?.success(
            NotificationEvent().apply {
                id = intent.getLongExtra(NOTIFICATION_EXTRA_ID_KEY, -1)
                jid = intent.getStringExtra(NOTIFICATION_EXTRA_JID_KEY)!!
                type = Api.NotificationEventType.MARK_AS_READ
                payload = null
                extra = extractPayloadMapFromIntent(intent)
            }.toList()
        )

        NotificationManagerCompat.from(context).cancel(intent.getLongExtra(MARK_AS_READ_ID_KEY, -1).toInt())
        dismissNotification(context, intent);
    }

    private fun handleReply(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent) ?: return
        val replyPayload = remoteInput.getCharSequence(REPLY_TEXT_KEY)
        MoxplatformAndroidPlugin.notificationSink?.success(
            NotificationEvent().apply {
                id = intent.getLongExtra(NOTIFICATION_EXTRA_ID_KEY, -1)
                jid = intent.getStringExtra(NOTIFICATION_EXTRA_JID_KEY)!!
                type = Api.NotificationEventType.REPLY
                payload = replyPayload.toString()
                extra = extractPayloadMapFromIntent(intent)
            }.toList()
        )

        val id = intent.getLongExtra(NOTIFICATION_EXTRA_ID_KEY, -1).toInt()
        if (id == -1) {
            Log.e(TAG, "Failed to find notification id for reply")
            return;
        }

        val notification = findActiveNotification(context, id)
        if (notification == null) {
            Log.e(TAG, "Failed to find notification for id $id")
            return
        }

        // Thanks https://medium.com/@sidorovroman3/android-how-to-use-messagingstyle-for-notifications-without-caching-messages-c414ef2b816c
        val recoveredStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(notification)!!
        val newStyle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            Notification.MessagingStyle(
                android.app.Person.Builder().apply {
                    setName(NotificationDataManager.getYou(context))

                    // Set an avatar, if we have one
                    val avatarPath = NotificationDataManager.getAvatarPath(context)
                    if (avatarPath != null) {
                        setIcon(
                            Icon.createWithAdaptiveBitmap(
                                BitmapFactory.decodeFile(avatarPath)
                            )
                        )
                    }
                }.build()
            )
        else Notification.MessagingStyle(NotificationDataManager.getYou(context))

        newStyle.apply {
            conversationTitle = recoveredStyle.conversationTitle
            recoveredStyle.messages.forEach {
                // Check if we have to request (or refresh) the content URI to be able to still
                // see the embedded image.
                val mime = it.extras.getString(NOTIFICATION_MESSAGE_EXTRA_MIME)
                val path = it.extras.getString(NOTIFICATION_MESSAGE_EXTRA_PATH)
                val message = Notification.MessagingStyle.Message(it.text, it.timestamp, it.sender)
                if (mime != null && path != null) {
                    // Request a new URI from the file provider to ensure we can still see the image
                    // in the notification
                    val fileUri = FileProvider.getUriForFile(
                        context,
                        MOXPLATFORM_FILEPROVIDER_ID,
                        File(path),
                    )
                    message.setData(
                        mime,
                        fileUri,
                    )

                    // As we're creating a new message, also recreate the additional metadata
                    message.extras.apply {
                        putString(NOTIFICATION_MESSAGE_EXTRA_MIME, mime)
                        putString(NOTIFICATION_MESSAGE_EXTRA_PATH, path)
                    }
                }

                // Append the old message
                addMessage(message)
            }
        }

        // Append our new message
        newStyle.addMessage(
            Notification.MessagingStyle.Message(
                replyPayload!!,
                Instant.now().toEpochMilli(),
                null as CharSequence?
            )
        )

        // Post the new notification
        val recoveredBuilder = Notification.Builder.recoverBuilder(context, notification).apply {
            style = newStyle
            setOnlyAlertOnce(true)
            setGroup(GROUP_KEY_MESSAGES)
        }
        NotificationManagerCompat.from(context).notify(id, recoveredBuilder.build())
    }

    private fun handleTap(context: Context, intent: Intent) {
        MoxplatformAndroidPlugin.notificationSink?.success(
            NotificationEvent().apply {
                id = intent.getLongExtra(NOTIFICATION_EXTRA_ID_KEY, -1)
                jid = intent.getStringExtra(NOTIFICATION_EXTRA_JID_KEY)!!
                type = Api.NotificationEventType.OPEN
                payload = null
                extra = extractPayloadMapFromIntent(intent)
            }.toList()
        )

        // Bring the app into the foreground
        val tapIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)!!
        context.startActivity(tapIntent)

        // Dismiss the notification
        dismissNotification(context, intent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        // TODO: We need to be careful to ensure that the Flutter engine is running.
        //       If it's not, we have to start it. However, that's only an issue when we expect to
        //       receive notifications while not running, i.e. Push Notifications.
        when (intent.action) {
            MARK_AS_READ_ACTION -> handleMarkAsRead(context, intent)
            REPLY_ACTION -> handleReply(context, intent)
            TAP_ACTION -> handleTap(context, intent)
        }
    }
}
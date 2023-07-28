package me.polynom.moxplatform_android

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.IconCompat
import java.io.File

object NotificationDataManager {
    var you: String = "You"
    var markAsRead: String = "Mark as read"
    var reply: String = "Reply"
    var avatarPath: String? = null
}

/// Show a messaging style notification described by @notification.
fun showMessagingNotification(context: Context, notification: Api.MessagingNotification) {
    // Build the actions
    // -> Reply action
    val remoteInput = RemoteInput.Builder(REPLY_TEXT_KEY).apply {
        setLabel(NotificationDataManager.reply)
    }.build()
    val replyIntent = Intent(context, NotificationReceiver::class.java).apply {
        action = REPLY_ACTION
        putExtra(NOTIFICATION_EXTRA_JID_KEY, notification.jid)
        putExtra(NOTIFICATION_EXTRA_ID_KEY, notification.id)
    }
    val replyPendingIntent = PendingIntent.getBroadcast(
        context.applicationContext,
        0,
        replyIntent,
        PendingIntent.FLAG_UPDATE_CURRENT,
    )
    val replyAction = NotificationCompat.Action.Builder(
        // TODO: Wrong icon?
        R.drawable.ic_service_icon,
        NotificationDataManager.reply,
        replyPendingIntent,
    ).apply {
        addRemoteInput(remoteInput)
        setAllowGeneratedReplies(true)
    }.build()

    // -> Mark as read action
    val markAsReadIntent = Intent(context, NotificationReceiver::class.java).apply {
        action = MARK_AS_READ_ACTION
        putExtra(NOTIFICATION_EXTRA_JID_KEY, notification.jid)
        putExtra(NOTIFICATION_EXTRA_ID_KEY, notification.id)
    }
    val markAsReadPendingIntent = PendingIntent.getBroadcast(
        context.applicationContext,
        0,
        markAsReadIntent,
        PendingIntent.FLAG_UPDATE_CURRENT,
    )
    val markAsReadAction = NotificationCompat.Action.Builder(
        // TODO: Wrong icon
        R.drawable.ic_service_icon,
        NotificationDataManager.markAsRead,
        markAsReadPendingIntent,
    ).build()

    // -> Tap action
    // Thanks https://github.com/MaikuB/flutter_local_notifications/blob/master/flutter_local_notifications/android/src/main/java/com/dexterous/flutterlocalnotifications/FlutterLocalNotificationsPlugin.java#L246
    val tapIntent = Intent(context, NotificationReceiver::class.java).apply {
        action = TAP_ACTION
        putExtra(NOTIFICATION_EXTRA_JID_KEY, notification.jid)
        putExtra(NOTIFICATION_EXTRA_ID_KEY, notification.id)
    }
    val tapPendingIntent = PendingIntent.getBroadcast(
        context,
        notification.id.toInt(),
        tapIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val selfPerson = Person.Builder().apply {
        setName(NotificationDataManager.you)

        // Set an avatar, if we have one
        if (NotificationDataManager.avatarPath != null) {
            setIcon(
                IconCompat.createWithAdaptiveBitmap(
                    BitmapFactory.decodeFile(NotificationDataManager.avatarPath),
                ),
            )
        }
    }.build()
    val style = NotificationCompat.MessagingStyle(selfPerson);
    for (message in notification.messages) {
        // Build the sender
        val sender = Person.Builder().apply {
            setName(message.sender)
            setKey(message.jid)

            // Set the avatar, if available
            if (message.avatarPath != null) {
                setIcon(
                    IconCompat.createWithAdaptiveBitmap(
                        BitmapFactory.decodeFile(message.avatarPath),
                    ),
                )
            }
        }.build()

        // Build the message
        val body = message.content.body ?: ""
        val msg = NotificationCompat.MessagingStyle.Message(
            body,
            message.timestamp,
            sender,
        )
        // If we got an image, turn it into a content URI and set it
        if (message.content.mime != null && message.content.path != null) {
            val fileUri = FileProvider.getUriForFile(
                context,
                MOXPLATFORM_FILEPROVIDER_ID,
                File(message.content.path),
            )
            msg.apply {
                setData(message.content.mime, fileUri)

                extras.apply {
                    putString(NOTIFICATION_MESSAGE_EXTRA_MIME, message.content.mime)
                    putString(NOTIFICATION_MESSAGE_EXTRA_PATH, message.content.path)
                }
            }
        }

        // Append the message
        style.addMessage(msg)
    }

    // Assemble the notification
    val finalNotification = NotificationCompat.Builder(context, notification.channelId).apply {
        setStyle(style)
        // TODO: I think this is wrong
        setSmallIcon(R.drawable.ic_service_icon)

        // Tap action
        setContentIntent(tapPendingIntent)

        // Notification actions
        addAction(replyAction)
        addAction(markAsReadAction)

        // Prevent no notification when we replied before
        setOnlyAlertOnce(false)
    }.build()

    // Post the notification
    NotificationManagerCompat.from(context).notify(
        notification.id.toInt(),
        finalNotification,
    )
}
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

/// Show a messaging style notification described by @notification.
fun showMessagingNotification(context: Context, notification: Api.MessagingNotification) {
    // Build the actions
    // -> Reply action
    val remoteInput = RemoteInput.Builder(REPLY_TEXT_KEY).apply {
        // TODO: i18n
        setLabel("Reply")
    }.build()
    val replyIntent = Intent(context, NotificationReceiver::class.java)
    val replyPendingIntent = PendingIntent.getBroadcast(
        context.applicationContext,
        0,
        replyIntent,
        PendingIntent.FLAG_UPDATE_CURRENT,
    )
    val replyAction = NotificationCompat.Action.Builder(
        // TODO: Wrong icon?
        R.drawable.ic_service_icon,
        // TODO: i18n
        "Reply",
        replyPendingIntent,
    ).apply {
        addRemoteInput(remoteInput)
    }.build()

    // -> Mark as read action
    val markAsReadIntent = Intent(context, NotificationReceiver::class.java).apply {
        action = MARK_AS_READ_ACTION
        // TODO: Put the JID here
        putExtra("jid", notification.jid)
    }
    val markAsReadPendingIntent = PendingIntent.getBroadcast(
        context.applicationContext,
        0,
        markAsReadIntent,
        0,
    )

    // -> Tap action
    // Thanks https://github.com/MaikuB/flutter_local_notifications/blob/master/flutter_local_notifications/android/src/main/java/com/dexterous/flutterlocalnotifications/FlutterLocalNotificationsPlugin.java#L246
    // TODO: Copy the interface of awesome_notifications
    val tapIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)!!.apply {
        putExtra("jid", notification.jid)
    }
    val tapPendingIntent = PendingIntent.getActivity(
        context,
        notification.id.toInt(),
        tapIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    // TODO: Use a person
    // TODO: i18n
    val style = NotificationCompat.MessagingStyle("Me");
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
                "me.polynom.moxplatform_android.fileprovider",
                File(message.content.path),
            )
            msg.setData(message.content.mime, fileUri)
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
        addAction(
            // TODO: Wrong icon
            R.drawable.ic_service_icon,
            // TODO: i18n
            "Mark as read",
            markAsReadPendingIntent,
        )
    }.build()

    // Post the notification
    NotificationManagerCompat.from(context).notify(
        notification.id.toInt(),
        finalNotification,
    )
}
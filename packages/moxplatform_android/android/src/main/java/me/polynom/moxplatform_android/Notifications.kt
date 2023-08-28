package me.polynom.moxplatform_android

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.IconCompat
import java.io.File
import java.lang.Exception

/*
 * Holds "persistent" data for notifications, like i18n strings. While not useful now, this is
 * useful for when the app is dead and we receive a notification.
 * */
object NotificationDataManager {
    private var you: String? = null
    private var markAsRead: String? = null
    private var reply: String? = null

    private var fetchedAvatarPath = false
    private var avatarPath: String? = null

    private fun getString(context: Context, key: String, fallback: String): String {
        return context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)!!.getString(key, fallback)!!
    }

    private fun setString(context: Context, key: String, value: String) {
        val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(key, value)
            .apply()
    }

    fun getYou(context: Context): String {
        if (you == null) you = getString(context, SHARED_PREFERENCES_YOU_KEY, "You")
        return you!!
    }

    fun setYou(context: Context, value: String) {
        setString(context, SHARED_PREFERENCES_YOU_KEY, value)
        you = value
    }

    fun getMarkAsRead(context: Context): String {
        if (markAsRead == null) markAsRead = getString(context, SHARED_PREFERENCES_MARK_AS_READ_KEY, "Mark as read")
        return markAsRead!!
    }

    fun setMarkAsRead(context: Context, value: String) {
        setString(context, SHARED_PREFERENCES_MARK_AS_READ_KEY, value)
        markAsRead = value
    }

    fun getReply(context: Context): String {
        if (reply != null) reply = getString(context, SHARED_PREFERENCES_REPLY_KEY, "Reply")
        return reply!!
    }

    fun setReply(context: Context, value: String) {
        setString(context, SHARED_PREFERENCES_REPLY_KEY, value)
        reply = value
    }

    fun getAvatarPath(context: Context): String? {
        if (avatarPath == null && !fetchedAvatarPath) {
            val path = getString(context, SHARED_PREFERENCES_AVATAR_KEY, "")
            if (path.isNotEmpty()) {
                avatarPath = path
            }
        }

        return avatarPath
    }

    fun setAvatarPath(context: Context, value: String) {
        setString(context, SHARED_PREFERENCES_AVATAR_KEY, value)
        fetchedAvatarPath = true
        avatarPath = value
    }
}

/// Show a messaging style notification described by @notification.
fun showMessagingNotification(context: Context, notification: Api.MessagingNotification) {
    // Build the actions
    // -> Reply action
    val remoteInput = RemoteInput.Builder(REPLY_TEXT_KEY).apply {
        setLabel(NotificationDataManager.getReply(context))
    }.build()
    val replyIntent = Intent(context, NotificationReceiver::class.java).apply {
        action = REPLY_ACTION
        putExtra(NOTIFICATION_EXTRA_JID_KEY, notification.jid)
        putExtra(NOTIFICATION_EXTRA_ID_KEY, notification.id)

        notification.extra?.forEach {
            putExtra("payload_${it.key}", it.value)
        }
    }
    val replyPendingIntent = PendingIntent.getBroadcast(
        context.applicationContext,
        0,
        replyIntent,
        PendingIntent.FLAG_MUTABLE,
    )
    val replyAction = NotificationCompat.Action.Builder(
        R.drawable.reply,
        NotificationDataManager.getReply(context),
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

        notification.extra?.forEach {
            putExtra("payload_${it.key}", it.value)
        }
    }
    val markAsReadPendingIntent = PendingIntent.getBroadcast(
        context.applicationContext,
        0,
        markAsReadIntent,
        PendingIntent.FLAG_IMMUTABLE,
    )
    val markAsReadAction = NotificationCompat.Action.Builder(
        R.drawable.mark_as_read,
        NotificationDataManager.getMarkAsRead(context),
        markAsReadPendingIntent,
    ).build()

    // -> Tap action
    // Thanks https://github.com/MaikuB/flutter_local_notifications/blob/master/flutter_local_notifications/android/src/main/java/com/dexterous/flutterlocalnotifications/FlutterLocalNotificationsPlugin.java#L246
    val tapIntent = Intent(context, NotificationReceiver::class.java).apply {
        action = TAP_ACTION
        putExtra(NOTIFICATION_EXTRA_JID_KEY, notification.jid)
        putExtra(NOTIFICATION_EXTRA_ID_KEY, notification.id)

        notification.extra?.forEach {
            putExtra("payload_${it.key}", it.value)
        }
    }
    val tapPendingIntent = PendingIntent.getBroadcast(
        context,
        notification.id.toInt(),
        tapIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    // Build the notification
    val selfPerson = Person.Builder().apply {
        setName(NotificationDataManager.getYou(context))

        // Set an avatar, if we have one
        val avatarPath = NotificationDataManager.getAvatarPath(context)
        if (avatarPath != null) {
            setIcon(
                IconCompat.createWithAdaptiveBitmap(
                    BitmapFactory.decodeFile(avatarPath),
                ),
            )
        }
    }.build()
    val style = NotificationCompat.MessagingStyle(selfPerson);
    style.isGroupConversation = notification.isGroupchat
    if (notification.isGroupchat) {
        style.conversationTitle = notification.title
    }

    for (i in notification.messages.indices) {
        val message = notification.messages[i]

        // Build the sender
        // NOTE: Note that we set it to null if message.sender == null because otherwise this results in
        //       a bogus Person object which messes with the "self-message" display as Android expects
        //       null in that case.
        val sender = if (message.sender == null)
            null
        else Person.Builder().apply {
            setName(message.sender)
            setKey(message.jid)

            // Set the avatar, if available
            if (message.avatarPath != null) {
                try {
                    setIcon(
                        IconCompat.createWithAdaptiveBitmap(
                            BitmapFactory.decodeFile(message.avatarPath),
                        ),
                    )
                } catch (ex: Throwable) {
                    Log.w(TAG, "Failed to open avatar at ${message.avatarPath}")
                }
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
        // NOTE: It's okay to use the service icon here as I cannot get Android to display the
        //       actual logo. So we'll have to make do with the silhouette and the color purple.
        setSmallIcon(R.drawable.ic_service_icon)
        color = Color.argb(255, 207, 74, 255)
        setColorized(true)

        // Tap action
        setContentIntent(tapPendingIntent)

        // Notification actions
        addAction(replyAction)
        addAction(markAsReadAction)

        // Groupchat title
        if (notification.isGroupchat) {
            setContentTitle(notification.title)
        }

        // Prevent grouping with the foreground service
        setGroup(GROUP_KEY_MESSAGES)

        setAllowSystemGeneratedContextualActions(true)
        setCategory(Notification.CATEGORY_MESSAGE)

        // Prevent no notification when we replied before
        setOnlyAlertOnce(false)
    }.build()

    // Post the notification
    NotificationManagerCompat.from(context).notify(
        notification.id.toInt(),
        finalNotification,
    )
}

fun showNotification(context: Context, notification: Api.RegularNotification) {
    val builtNotification = NotificationCompat.Builder(context, notification.channelId).apply {
        setContentTitle(notification.title)
        setContentText(notification.body)

        when (notification.icon) {
            Api.NotificationIcon.ERROR -> setSmallIcon(R.drawable.error)
            Api.NotificationIcon.WARNING -> setSmallIcon(R.drawable.warning)
            Api.NotificationIcon.NONE -> {}
        }

        setGroup(GROUP_KEY_OTHER)
    }.build()

    // Post the notification
    NotificationManagerCompat.from(context).notify(notification.id.toInt(), builtNotification)
}
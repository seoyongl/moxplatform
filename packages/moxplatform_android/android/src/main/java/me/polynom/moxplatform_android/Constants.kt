package me.polynom.moxplatform_android

// The tag we use for logging.
const val TAG = "Moxplatform"

// The size of the buffer to hashing, encryption, and decryption in bytes.
const val BUFFER_SIZE = 8096

const val REPLY_ACTION = "reply";
// The data key for text entered in the notification's reply field
const val REPLY_TEXT_KEY = "key_reply_text"

// The action for pressing the "Mark as read" button on a notification
const val MARK_AS_READ_ACTION = "mark_as_read"
// The key for the notification id to mark as read
const val MARK_AS_READ_ID_KEY = "notification_id"

// TODO: Maybe try again to rewrite the entire plugin in Kotlin
//const val METHOD_CHANNEL_KEY = "me.polynom.moxplatform_android"
//const val BACKGROUND_METHOD_CHANNEL_KEY = METHOD_CHANNEL_KEY + "_bg"

// https://github.com/ekasetiawans/flutter_background_service/blob/e427f3b70138ec26f9671c2617f9061f25eade6f/packages/flutter_background_service_android/android/src/main/java/id/flutter/flutter_background_service/BootReceiver.java#L20
//const val WAKELOCK_DURATION = 10*60*1000L;

// The name of the wakelock the background service manager holds.
//const val SERVICE_WAKELOCK_NAME = "BackgroundService.Lock"

//const val DATA_RECEIVER_METHOD_NAME = "dataReceived"

// Shared preferences keys
//const val SHARED_PREFERENCES_KEY = "me.polynom.moxplatform_android"
//const val SP_MANUALLY_STOPPED_KEY = "manually_stopped"
//const val SP_ENTRYPOINT_KEY = "entrypoint_handle"
//const val SP_EXTRA_DATA_KEY = "extra_data"
//const val SP_AUTO_START_AT_BOOT_KEY = "auto_start_at_boot"
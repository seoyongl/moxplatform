package me.polynom.moxplatform_android

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

/*
 * Uses Android's direct share API to create dynamic share targets that are compatible
 * with share_handler's media handling.
 * */
fun recordSentMessage(context: Context, name: String, jid: String, avatarPath: String?, fallbackIconType: Int) {
    val pkgName = context.packageName
    val intent = Intent(context, Class.forName("$pkgName.MainActivity")).apply {
        action = Intent.ACTION_SEND

        // Compatibility with share_handler
        putExtra("conversationIdentifier", jid)
    }

    val shortcutTarget = "$pkgName.dynamic_share_target"
    val shortcutBuilder = ShortcutInfoCompat.Builder(context, jid).apply {
        setShortLabel(name)
        setIsConversation()
        setCategories(setOf(shortcutTarget))
        setIntent(intent)
        setLongLived(true)
    }

    val personBuilder = Person.Builder().apply {
        setKey(jid)
        setName(name)
    }

    // Either set an avatar image OR a fallback icon
    if (avatarPath != null) {
        val icon = IconCompat.createWithAdaptiveBitmap(
            BitmapFactory.decodeFile(avatarPath),
        )
        shortcutBuilder.setIcon(icon)
        personBuilder.setIcon(icon)
    } else {
        val resourceId = when(fallbackIconType) {
            0 -> R.mipmap.person
            1 -> R.mipmap.notes
            // "Fallthrough"
            else -> R.mipmap.person
        }
        val icon = IconCompat.createWithResource(context, resourceId)
        shortcutBuilder.setIcon(icon)
        personBuilder.setIcon(icon)
    }

    shortcutBuilder.setPerson(personBuilder.build())
    ShortcutManagerCompat.addDynamicShortcuts(
        context,
        listOf(shortcutBuilder.build()),
    )
}

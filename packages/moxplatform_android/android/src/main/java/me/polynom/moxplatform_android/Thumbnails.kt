package me.polynom.moxplatform_android

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import java.io.FileOutputStream

/*
 * Generate a video thumbnail using the first frame of the video at @src. Afterwards, scale it
 * down such that its width is equal to @maxWidth (while keeping the aspect ratio) and write it to
 * @dest.
 *
 * If everything went well, returns true. If we're unable to generate the thumbnail, returns false.
 * */
fun generateVideoThumbnailImplementation(src: String, dest: String, maxWidth: Long): Boolean {
    try {
        val mmr = MediaMetadataRetriever().apply {
            setDataSource(src)
        }
        val unscaledThumbnail = mmr.getFrameAtTime(0) ?: return false

        // Scale down the thumbnail while keeping the aspect ratio
        val scalingFactor = maxWidth.toDouble() / unscaledThumbnail.width;
        Log.d(TAG, "Scaling to $maxWidth from ${unscaledThumbnail.width} with scalingFactor $scalingFactor");
        val thumbnail = Bitmap.createScaledBitmap(
            unscaledThumbnail,
            (unscaledThumbnail.width * scalingFactor).toInt(),
            (unscaledThumbnail.height * scalingFactor).toInt(),
            false,
        )

        // Write it to the destination file
        val fos = FileOutputStream(dest)
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 75, fos)
        fos.flush()
        fos.close()
        return true;
    } catch (ex: Exception) {
        Log.e(TAG, "Failed to create thumbnail for $src: ${ex.message}")
        ex.printStackTrace()
        return false;
    }
}
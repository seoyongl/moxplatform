package me.polynom.moxplatform_android

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


object RequestTracker {
    val requests: MutableMap<Int, Api.Result<Any>> = mutableMapOf()
}

const val PICK_FILE_REQUEST = 41;
const val PICK_FILES_REQUEST = 42;

fun genericFilePickerRequest(activity: Activity?, pickMultiple: Boolean, result: Api.Result<List<String>>) {
    val pickIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"

        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, pickMultiple);
    }

    RequestTracker.requests[PICK_FILE_REQUEST] = result as Api.Result<Any>;
    activity?.startActivityForResult(pickIntent, PICK_FILE_REQUEST)
}

fun filePickerRequest(
    context: Context,
    activity: Activity?,
    type: Api.FilePickerType,
    pickMultiple: Boolean,
    result: Api.Result<List<String>>
) {
    if (type == Api.FilePickerType.GENERIC) {
        return genericFilePickerRequest(activity, pickMultiple, result)
    }

    val pickerType = when (type) {
        Api.FilePickerType.IMAGE -> ActivityResultContracts.PickVisualMedia.ImageOnly
        Api.FilePickerType.VIDEO -> ActivityResultContracts.PickVisualMedia.VideoOnly
        Api.FilePickerType.IMAGE_AND_VIDEO -> ActivityResultContracts.PickVisualMedia.ImageAndVideo
        // TODO
        Api.FilePickerType.GENERIC -> ActivityResultContracts.PickVisualMedia.ImageAndVideo
    }

    val pick = when (pickMultiple) {
        false -> ActivityResultContracts.PickVisualMedia()
        true -> ActivityResultContracts.PickMultipleVisualMedia()
    }

    val requestCode = if (pickMultiple) PICK_FILES_REQUEST else PICK_FILE_REQUEST
    val pickIntent = pick.createIntent(context, PickVisualMediaRequest(pickerType))
    RequestTracker.requests[requestCode] = result as Api.Result<Any>
    Log.d(TAG, "Tracked size ${RequestTracker.requests.size}")

    if (activity == null) {
        Log.w(TAG, "Activity is null")
    }
    activity?.startActivityForResult(pickIntent, requestCode);
}

/**
 * Copies the file from the given content URI to a temporary directory, retaining the original
 * file name if possible.
 *
 *
 * Each file is placed in its own directory to avoid conflicts according to the following
 * scheme: {cacheDir}/{randomUuid}/{fileName}
 *
 *
 * File extension is changed to match MIME type of the file, if known. Otherwise, the extension
 * is left unchanged.
 *
 *
 * If the original file name is unknown, a predefined "image_picker" filename is used and the
 * file extension is deduced from the mime type (with fallback to ".jpg" in case of failure).
 */
fun getPathFromUri(context: Context, uri: Uri): String? {
    try {
        context.contentResolver.openInputStream(uri).use { inputStream ->
            val uuid = UUID.randomUUID().toString()
            val targetDirectory = File(context.cacheDir, uuid)
            targetDirectory.mkdir()
            // TODO(SynSzakala) according to the docs, `deleteOnExit` does not work reliably on Android; we should preferably
            //  just clear the picked files after the app startup.
            targetDirectory.deleteOnExit()
            var fileName = getImageName(context, uri)
            var extension = getImageExtension(context, uri)
            if (fileName == null) {
                Log.w("FileUtils", "Cannot get file name for $uri")
                if (extension == null) extension = ".jpg"
                fileName = "image_picker$extension"
            } else if (extension != null) {
                fileName = getBaseName(fileName) + extension
            }
            val file = File(targetDirectory, fileName)
            FileOutputStream(file).use { outputStream ->
                copy(inputStream!!, outputStream)
                return file.path
            }
        }
    } catch (e: IOException) {
        // If closing the output stream fails, we cannot be sure that the
        // target file was written in full. Flushing the stream merely moves
        // the bytes into the OS, not necessarily to the file.
        return null
    } catch (e: SecurityException) {
        // Calling `ContentResolver#openInputStream()` has been reported to throw a
        // `SecurityException` on some devices in certain circumstances. Instead of crashing, we
        // return `null`.
        //
        // See https://github.com/flutter/flutter/issues/100025 for more details.
        return null
    }
}

/** @return extension of image with dot, or null if it's empty.
 */
private fun getImageExtension(context: Context, uriImage: Uri): String? {
    val extension: String?
    extension = try {
        if (uriImage.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uriImage))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(
                Uri.fromFile(File(uriImage.path)).toString()
            )
        }
    } catch (e: Exception) {
        return null
    }
    return if (extension == null || extension.isEmpty()) {
        null
    } else ".$extension"
}

/** @return name of the image provided by ContentResolver; this may be null.
 */
private fun getImageName(context: Context, uriImage: Uri): String? {
    queryImageName(context, uriImage).use { cursor ->
        return if (cursor == null || !cursor.moveToFirst() || (cursor.columnCount < 1)) null else cursor.getString(
            0
        )
    }
}

private fun queryImageName(context: Context, uriImage: Uri): Cursor? {
    return context
        .contentResolver
        .query(uriImage, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)
}

@Throws(IOException::class)
private fun copy(`in`: InputStream, out: OutputStream) {
    val buffer = ByteArray(4 * 1024)
    var bytesRead: Int
    while (`in`.read(buffer).also { bytesRead = it } != -1) {
        out.write(buffer, 0, bytesRead)
    }
    out.flush()
}

private fun getBaseName(fileName: String): String {
    val lastDotIndex = fileName.lastIndexOf('.')
    return if (lastDotIndex < 0) {
        fileName
    } else fileName.substring(0, lastDotIndex)
    // Basename is everything before the last '.'.
}

fun onActivityResultImpl(context: Context, requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    Log.d(TAG, "Got result for $requestCode with result $resultCode (${data?.action})")
    if (requestCode == PICK_FILE_REQUEST || requestCode == PICK_FILES_REQUEST) {
        Log.d(TAG, "Extra data ${data?.data}")
        val result = RequestTracker.requests.remove(requestCode);
        if (result == null) {
            Log.w(TAG, "Untracked response.")
            return false;
        }

        if (resultCode != Activity.RESULT_OK) {
            // No files picked
            result!!.success(listOf<String>())
            return true;
        }

        val pickedMultiple = requestCode == PICK_FILES_REQUEST
        val pickedFiles = mutableListOf<String>()
        if (pickedMultiple) {
            val intentUris = data!!.clipData
            if (data!!.clipData != null) {
                for (i in 0 until data!!.clipData!!.itemCount) {
                    val path = getPathFromUri(context, data!!.clipData!!.getItemAt(i).uri)
                    if (path != null) {
                        pickedFiles.add(path )
                    }
                }
            }
        } else {
            val path = getPathFromUri(context, data!!.data!!)
            if (path != null) {
                pickedFiles.add(path )
            }
        }

        result!!.success(pickedFiles)
        return true;
    }

    return false;
}
package com.example.myassignmenttwo.permission

import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import androidx.compose.runtime.SideEffect
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.core.content.ContextCompat


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermission(
    permission: String,
    rationale: String,
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { }
) {
    val permissionState = rememberPermissionState(permission)

    when {
        permissionState.status.isGranted -> {
            content()
        }
        permissionState.status.shouldShowRationale -> {
            Rationale(
                text = rationale,
                onRequestPermission = { permissionState.launchPermissionRequest() }
            )
        }
        else -> {
            SideEffect {
                permissionState.launchPermissionRequest()
            }
            permissionNotAvailableContent()
        }
    }
}

@Composable
fun Rationale(text: String, onRequestPermission: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = { Text("Permission required") },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("OK")
            }
        }
    )
}

fun getMediaStoreUri(context: Context, uriString: String): Uri {
    val uri = Uri.parse(uriString)
    Log.d("MediaStore", "Original URI: $uri")

    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> handleAndroid10Plus(context, uri)
        DocumentsContract.isDocumentUri(context, uri) -> handleDocumentUri(context, uri)
        uri.scheme == "content" -> handleContentUri(context, uri)
        uri.scheme == "file" -> handleFileUri(uri)
        else -> uri
    }.also { Log.d("MediaStore", "Final URI: $it") }
}

private fun handleAndroid10Plus(context: Context, uri: Uri): Uri {
    return when {
        isMediaDocument(uri) -> {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]
            val contentUri = getContentUriForType(type)
            getMediaContentUri(context, contentUri, "_id=?", arrayOf(split[1]))
        }
        isDownloadsDocument(uri) -> {
            val docId = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), docId.toLong())
            getMediaContentUri(context, contentUri, null, null)
        }
        isExternalStorageDocument(uri) -> uri // External storage documents are typically already properly formatted
        else -> {
            if (hasAccessMediaLocationPermission(context)) {
                try {
                    MediaStore.setRequireOriginal(uri)
                } catch (e: Exception) {
                    Log.e("MediaStore", "Error setting require original", e)
                    uri
                }
            } else {
                Log.w("MediaStore", "ACCESS_MEDIA_LOCATION permission not granted")
                uri
            }
        }
    }
}

private fun handleDocumentUri(context: Context, uri: Uri): Uri {
    val docId = DocumentsContract.getDocumentId(uri)
    val split = docId.split(":")
    val type = split[0]
    val contentUri = getContentUriForType(type)
    return getMediaContentUri(context, contentUri, "_id=?", arrayOf(split[1]))
}

private fun handleContentUri(context: Context, uri: Uri): Uri {
    return when (uri.authority) {
        "com.android.providers.media.documents" -> handleMediaDocuments(context, uri)
        "com.android.providers.downloads.documents" -> handleDownloadsDocuments(context, uri)
        else -> uri
    }
}

private fun handleFileUri(uri: Uri): Uri {
    return Uri.fromFile(java.io.File(uri.path!!))
}

private fun handleMediaDocuments(context: Context, uri: Uri): Uri {
    val docId = DocumentsContract.getDocumentId(uri)
    val split = docId.split(":")
    val type = split[0]
    val contentUri = getContentUriForType(type)
    return getMediaContentUri(context, contentUri, "_id=?", arrayOf(split[1]))
}

private fun handleDownloadsDocuments(context: Context, uri: Uri): Uri {
    val id = DocumentsContract.getDocumentId(uri)
    return ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id.toLong())
}

private fun getContentUriForType(type: String): Uri {
    return when (type) {
        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        else -> MediaStore.Files.getContentUri("external")
    }
}

private fun getMediaContentUri(context: Context, contentUri: Uri, selection: String?, selectionArgs: Array<String>?): Uri {
    val projection = arrayOf(MediaStore.MediaColumns._ID)
    var cursor: Cursor? = null
    try {
        cursor = context.contentResolver.query(contentUri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val id = cursor.getLong(idColumn)
            return ContentUris.withAppendedId(contentUri, id)
        }
    } catch (e: Exception) {
        Log.e("MediaStore", "Error getting media content URI", e)
    } finally {
        cursor?.close()
    }
    return contentUri
}

private fun isMediaDocument(uri: Uri) = uri.authority == "com.android.providers.media.documents"
private fun isDownloadsDocument(uri: Uri) = uri.authority == "com.android.providers.downloads.documents"
private fun isExternalStorageDocument(uri: Uri) = uri.authority == "com.android.externalstorage.documents"

private fun hasAccessMediaLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED
}

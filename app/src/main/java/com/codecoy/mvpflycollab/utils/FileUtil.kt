package com.codecoy.mvpflycollab.utils
import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


open class FileUtil(
    private val context: Context
) {

    companion object {
        private const val PERM_REQ_CODE_READ_WRITE = 1000
    }

    val externalStoragePath get() = Environment.getExternalStorageDirectory().path

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }


    fun requestPermission(permissions: Array<String>, requestCode: Int) {
        context.startActivity(Intent(context, context.javaClass))
        ActivityCompat.requestPermissions(context as Activity, permissions, requestCode)
    }

    fun requestWritePermission() {
        requestPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERM_REQ_CODE_READ_WRITE)
    }

    fun requestReadPermission() {
        requestPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERM_REQ_CODE_READ_WRITE)
    }

    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        // For MediaStore URIs
        if (uri.scheme == "content") {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return it.getString(columnIndex)
                }
            }
        }

        // For general URIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                if ("com.android.providers.media.documents" == uri.authority) {
                    val split = docId.split(":").toTypedArray()
                    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    val cursor: Cursor? = context.contentResolver.query(contentUri, null, selection, selectionArgs, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                            return it.getString(columnIndex)
                        }
                    }
                } else if ("com.android.providers.downloads.documents" == uri.authority) {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(docId)
                    )
                    return getDataColumn(context, contentUri, null, null)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }
}
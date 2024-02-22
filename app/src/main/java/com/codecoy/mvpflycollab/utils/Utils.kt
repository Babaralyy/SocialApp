package com.codecoy.mvpflycollab.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object Utils {


    private const val PREF_NAME = "userData"
    private const val KEY_USER_DATA = "uData"

    private lateinit var sharedPreferences: SharedPreferences


    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                realPath = cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return realPath
    }

    // UPDATED!
    fun getPath(context: Context, uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri!!, projection, null, null, null)
        return if (cursor != null) {
            val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }

    fun getRealPathFromURI(contentResolver: ContentResolver, uri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val projection = arrayOf(MediaStore.Video.Media.DATA)
            cursor = contentResolver.query(uri, projection, null, null, null)
            cursor?.let {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                it.moveToFirst()
                it.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
    }

    fun getFileFromPath(filePath: String): File? {
        return try {
            File(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getPartFromFile(file: File): MultipartBody.Part{
        return MultipartBody.Part.createFormData("video", file.name, RequestBody.create("video/*".toMediaTypeOrNull(), file))
    }


    fun createTextPart(text: String): MultipartBody.Part {
        val requestBody = text.toRequestBody("text/plain".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("text", null, requestBody)
    }

    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        // For MediaStore URIs
        if (uri.scheme == "content") {
            val projection = arrayOf(MediaStore.Video.Media.DATA)
            val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    return it.getString(columnIndex)
                }
            }
        }

        // For general URIs
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority) {
                val split = docId.split(":").toTypedArray()
                val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                val cursor: Cursor? = context.contentResolver.query(contentUri, null, selection, selectionArgs, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndex(MediaStore.Video.Media.DATA)
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
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    fun saveUserToSharedPreferences(context: Context, userData: UserLoginData) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(userData)
        editor.putString(KEY_USER_DATA, json)
        editor.apply()
    }

    fun getUserFromSharedPreferences(context: Context): UserLoginData? {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(KEY_USER_DATA, null)
        return gson.fromJson(json, UserLoginData::class.java)
    }

}
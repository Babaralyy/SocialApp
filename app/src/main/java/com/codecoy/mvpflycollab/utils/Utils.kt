package com.codecoy.mvpflycollab.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore
import com.codecoy.mvpflycollab.datamodels.AllActivitiesData
import com.codecoy.mvpflycollab.datamodels.CalendarStoryData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object Utils {


    private const val PREF_NAME = "userData"
    private const val KEY_USER_DATA = "uData"

    var storyDetail: CalendarStoryData?= null
    var allActivitiesData: AllActivitiesData?= null
    var videoUrl: String?= null
    var isFromProfile: Boolean= false
    var isUserProfile: Boolean= false

    var scrollPosition: Int = 0
    var userId: String? = null

    var receiverId: Int = 0
    var socketId: String? = null

    private lateinit var sharedPreferences: SharedPreferences



    fun getFileFromPath(filePath: String): File? {
        return try {
            File(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getPartFromFile(type:String, param: String, file: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            param,
            file.name,
            RequestBody.create(type.toMediaTypeOrNull(), file)
        )
    }


    fun createTextRequestBody(text: String): RequestBody {
        // Define the media type for the text (you can adjust the media type based on your requirements)
        val mediaType = "multipart/form-data".toMediaTypeOrNull()

        // Create the RequestBody using the text and media type
        return RequestBody.create(mediaType, text)
    }


     fun getRealPathFromImgURI(context: Context,uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return filePath ?: ""
    }

     fun getRealPathFromVidURI(context: Context,uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        cursor?.moveToFirst()
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return filePath ?: ""
    }


     fun getDateFromComponents(year: Int, month: Int, dayOfMonth: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        return calendar.time
    }

     fun formatGeorgiaDate(currentDate: Calendar): String {
         val calendar = Calendar.getInstance()
         calendar.time = currentDate.time
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)

    }

    fun formatDate(currentDate: Date): String {
        val dateFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        return sdf.format(currentDate)
    }

    fun getCurrentTime(currentTime: Date): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(currentTime)
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

    fun clearSharedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}
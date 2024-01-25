package com.codecoy.mvpflycollab.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.codecoy.mvpflycollab.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

object Constant {

    const val TAG = "TAG"
    private const val BASE_URL = "https://codecoyapps.com/fly_colab/"
    const val MEDIA_BASE_URL = "https://codecoyapps.com/fly_colab/public/storage/"

    private var httpClient: OkHttpClient = OkHttpClient.Builder()
        .hostnameVerifier { _, _ ->
            HttpsURLConnection.getDefaultHostnameVerifier()
            true
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS) .addInterceptor { chain ->
            try {
                chain.proceed(chain.request())
            } catch (e: SocketTimeoutException) {
                // Handle timeout exception here
                throw IOException("Request timed out")
            }
        }
        .build()

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient).build()
    }

    fun getDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_lay)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}
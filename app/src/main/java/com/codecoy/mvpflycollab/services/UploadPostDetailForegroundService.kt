package com.codecoy.mvpflycollab.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.codecoy.mvpflycollab.viewmodels.PlaylistViewModel

class UploadPostDetailForegroundService : Service() {
    private lateinit var viewModel: PlaylistViewModel
    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Perform Retrofit call


        // Return START_NOT_STICKY since the service is not meant to be restarted
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun setUpViewModel() {

    }

}


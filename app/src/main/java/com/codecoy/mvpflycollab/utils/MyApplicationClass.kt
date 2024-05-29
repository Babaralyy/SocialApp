package com.codecoy.mvpflycollab.utils

import android.app.Application
import android.util.Log
import com.codecoy.mvpflycollab.chat.socket.SocketManager
import com.codecoy.mvpflycollab.utils.Constant.TAG

class MyApplicationClass : Application() {

    companion object {
        var socketManager: SocketManager? = null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "MyApplicationClass:: onCreate")


    }

}
package com.codecoy.mvpflycollab.chat.socket

import android.util.Log
import com.codecoy.mvpflycollab.utils.Constant
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class SocketManager {
    companion object{
         var socket: Socket? = null
    }
    init {
        try {
            socket = IO.socket("http://13.49.66.85:5503/")
        } catch (e: URISyntaxException) {
            Log.i(Constant.TAG, "connectToSocket:: ${e.printStackTrace()}")
            e.printStackTrace()
        }
    }

    fun connect() {
        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun isConnected(): Boolean {
        return socket?.connected() ?: false
    }


    fun sendMessage() {
        val message = Gson().toJson(Chat("Online", "1", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vMTI3LjAuMC4xOjgwMDAvYXBpL2xvZ2luIiwiaWF0IjoxNzExNzA1OTgzLCJleHAiOjE3NDMyNDE5ODMsIm5iZiI6MTcxMTcwNTk4MywianRpIjoiSFg0enM1WkRRQVprWnRmMCIsInN1YiI6IjIiLCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.Y5-DdV8gElPkgpNJtVJ24c5C4Z3IEl-nNFaQIPy2dtQ"), Chat::class.java)
        socket?.emit("message", message)
    }
}
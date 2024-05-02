package com.codecoy.mvpflycollab.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.chat.socket.SocketManager
import com.codecoy.mvpflycollab.utils.Constant.TAG
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URISyntaxException
import kotlin.coroutines.resumeWithException


class MainActivity : AppCompatActivity() {


    private val socketManager = SocketManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inIt()
    }

    private fun inIt() {
        try {
            GlobalScope.launch {
                try {
                    connectToSocket()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }


    private suspend fun connectToSocket() {
        withContext<Socket?>(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    socketManager.connect()

                    SocketManager.socket?.on("user_got_online") {
                        runOnUiThread {
                            Log.i(TAG, "main socket:: EVENT_CONNECT ${it[0]}")
                        }
//                        continuation.resume(socket)
                    }
                    SocketManager.socket?.on(Socket.EVENT_CONNECT_ERROR) { error ->
//                        continuation.resumeWithException(error as Throwable)
                        runOnUiThread {
                            Log.i(TAG, "main socket:: $error ")
                        }

                    }
                    SocketManager.socket?.on(Socket.EVENT_DISCONNECT) {
//                        continuation.resumeWithException(TimeoutException("Connection timed out"))
                        runOnUiThread {
                            Log.i(TAG, "main socket:: EVENT_DISCONNECT $it ")
                        }

                    }
                } catch (e: URISyntaxException) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }


    private fun emmitEvent() {
        val jSONObject = JSONObject()
        jSONObject.put("message", "Online")
        jSONObject.put("user_id", "1")
        jSONObject.put(
            "token",
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vMTI3LjAuMC4xOjgwMDAvYXBpL2xvZ2luIiwiaWF0IjoxNzExNzA1OTgzLCJleHAiOjE3NDMyNDE5ODMsIm5iZiI6MTcxMTcwNTk4MywianRpIjoiSFg0enM1WkRRQVprWnRmMCIsInN1YiI6IjIiLCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.Y5-DdV8gElPkgpNJtVJ24c5C4Z3IEl-nNFaQIPy2dtQ"
        )
        SocketManager.socket?.emit("set_online", jSONObject)
        Log.i(TAG, "main socket:: emit")
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    override fun onResume() {
        super.onResume()
        emmitEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.socket.let {
            it?.disconnect()
            it?.off()
        }
    }
}
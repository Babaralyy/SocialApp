package com.codecoy.mvpflycollab.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.chat.socket.SocketManager
import com.codecoy.mvpflycollab.datamodels.OnlineUserData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
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
    private var currentUser: UserLoginData? = null

    var onlineUsersMutableLiveData = MutableLiveData<MutableList<OnlineUserData>>()
    private lateinit var onlineUsersList: MutableList<OnlineUserData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inIt()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun inIt() {
        onlineUsersList = mutableListOf()
        currentUser = Utils.getUserFromSharedPreferences(this)

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


    override fun onNewIntent(intent: Intent?) {

        super.onNewIntent(intent)
    }

    private suspend fun connectToSocket() {
        withContext<Socket?>(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    socketManager.connect()



                    SocketManager.socket?.on("user_got_online") {
                        runOnUiThread {
                            val jsonObject = it[0] as JSONObject
                            Log.i(TAG, "main socket:: message $jsonObject")
                          /*  jsonObject.let {
                                val jsonData = it.getJSONObject("data")
                                jsonData.let { it1 ->
                                    val userData = it1.getJSONObject("response")
                                    userData.let { it2 ->
                                        val onlineUserData = OnlineUserData(
                                            it2.getInt("id"),
                                            it2.getString("profile_img"),
                                            it2.getString("name"),
                                            it2.getString("username"),
                                            it2.getString("phone"),
                                            it2.getString("email"),
                                            it2.getString("email_verified_at"),
                                            it2.getString("device_token"),
                                            it2.getString("website_url"),
                                            it2.getString("about_me"),
                                            it2.getString("socket_id"),
                                            it2.getBoolean("online"),
                                            it2.getString("created_at"),
                                            it2.getString("updated_at"),
                                        )

                                        if (onlineUserData.id != currentUser?.id){
                                            onlineUsersList.removeIf { it3 ->
                                                it3.id == onlineUserData.id
                                            }
                                            onlineUsersList.add(onlineUserData)
                                            onlineUsersMutableLiveData.value = onlineUsersList
                                        }


                                    }
                                }
                            }*/

                        }
                    }
                    SocketManager.socket?.on(Socket.EVENT_CONNECT_ERROR) { error ->
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.i(TAG, "main socket:: $error ")
                        }
                    }
                    SocketManager.socket?.on(Socket.EVENT_DISCONNECT) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.i(TAG, "main socket:: message $it")
                        }
                    }
                } catch (e: URISyntaxException) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }


    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    override fun onResume() {
        super.onResume()
        emmitOnlineEvent()
    }

    private fun emmitOnlineEvent() {
        val jSONObject = JSONObject()
        jSONObject.put("message", "Online")
        jSONObject.put("user_id", currentUser?.id.toString())
        jSONObject.put(
            "token",
            "Bearer " + currentUser?.token.toString()
        )
        SocketManager.socket?.emit("set_online", jSONObject)
        Log.i(TAG, "main socket:: emit")
    }

    private fun emmitOfflineEvent() {
        val jSONObject = JSONObject()
        jSONObject.put("message", "Disconnect")
        jSONObject.put("user_id", currentUser?.id.toString())
        jSONObject.put(
            "token",
            "Bearer " + currentUser?.token.toString()
        )
        SocketManager.socket?.emit("disconnect_user", jSONObject)
        Log.i(TAG, "main socket:: disconnect emit")
    }


    override fun onStop() {
        emmitOfflineEvent()
        super.onStop()
    }

    override fun onDestroy() {
        SocketManager.socket.let {
            SocketManager.socket?.disconnect()
            SocketManager.socket?.off()
        }
        super.onDestroy()
    }
}
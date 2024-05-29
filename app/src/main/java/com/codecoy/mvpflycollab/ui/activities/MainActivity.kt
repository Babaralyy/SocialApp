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


    private var currentUser: UserLoginData? = null

    var onlineUsersMutableLiveData = MutableLiveData<MutableList<OnlineUserData>>()
    private lateinit var onlineUsersList: MutableList<OnlineUserData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inIt()
    }


    private fun inIt() {
        onlineUsersList = mutableListOf()
        currentUser = Utils.getUserFromSharedPreferences(this)
    }


/*    override fun onNewIntent(intent: Intent?) {

        super.onNewIntent(intent)
    }*/


    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
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
           /* SocketManager.socket?.off()*/
        }
        super.onDestroy()
    }
}
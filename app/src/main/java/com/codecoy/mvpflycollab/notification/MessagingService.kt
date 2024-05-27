package com.codecoy.mvpflycollab.notification


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.utils.Utils.CHANNEL_ID_
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject


class MessagingService : FirebaseMessagingService() {

    private val bookingGroup = "com.android.example.AUDIO"
    private var summaryId: Long = 1


    override fun onNewToken(deviceToken: String) {
        super.onNewToken(deviceToken)

        Log.i(TAG, "onNewToken: deviceToken $deviceToken")

        Utils.deviceTokenIntoPref(this, "tokenInfo", deviceToken)

    }


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.i(TAG, "onMessageReceived:: onMessageReceived ${p0.data}")
        val state = Utils.fetchNotificationStateFromPref(this)
        Log.i(TAG, "onMessageReceived:: state $state")
        if (state) {
            showNotification(p0.data)
        }
    }

    private fun showNotification(data: MutableMap<String, String>) {

        val bundle = Bundle()


        val notificationPendingIntent: PendingIntent? = when (data["title"]) {
            "Like", "Comment" -> {

                val jsonObject = JSONObject()
                jsonObject.put("post_id", data["post_id"].toString())

                bundle.putString("json_data", jsonObject.toString())

                // Create a PendingIntent to navigate to the NotificationFragment
                NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.mainFragment)
                    .setArguments(bundle)
                    .createPendingIntent()
            }

            "Chat" -> {

                val jsonObject = JSONObject()
                jsonObject.put("receiver_id", data["receiver_id"]?.toInt() ?: 0)
                jsonObject.put("sender_id", data["sender_id"]?.toInt() ?: 0)
                jsonObject.put("sender_name", data["sender_name"])
                jsonObject.put("sender_socket_id", data["sender_socket_id"])


                bundle.putString("json_data", jsonObject.toString())

                // Create a PendingIntent to navigate to the NotificationFragment
                NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.chatFragment)
                    .setArguments(bundle)
                    .createPendingIntent()
            }

            else -> null
        }

        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID_, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(data["title"])
            .setContentText(data["body"])
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(notificationPendingIntent)
            .setGroup(bookingGroup)
            .setGroupSummary(true)
            .setAutoCancel(true)


        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(
                    this@MessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "onMessageReceived:: notify if")
                notify((++summaryId).toInt(), builder.build())
            } else {
                Log.i(TAG, "onMessageReceived:: notify else")
            }
        }
    }

}
package com.example.homework_recyclerview.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.convertor.R
import com.example.homework_recyclerview.presentation.movies.Movies
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


private const val CHANNEL_ID = "1234"


class MyFireBaseMessagingService: FirebaseMessagingService(){
    override fun onMessageReceived(message: RemoteMessage) {
        createNotificationChannel()
        displayNotification(message)
    }

    override fun onNewToken(token: String) {
        Log.d("TAG", "Refreshed token: $token")
    }

    private fun displayNotification(message: RemoteMessage) {

        val title = message.data["title"]
        val body = message.data["body"]

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.image_1_2)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(getPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(Int.MIN_VALUE, notification)
    }

    private fun getPendingIntent(): PendingIntent = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lesson 27 Channel"
            val descriptionText = "This is our first channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
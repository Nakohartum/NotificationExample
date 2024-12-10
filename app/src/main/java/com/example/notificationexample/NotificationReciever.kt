package com.example.notificationexample

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Random

class NotificationReciever: BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val notification = NotificationCompat.Builder(context!!, "MainChannel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText("Test")
            .setContentText("Test")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(Random().nextInt(), notification)

    }

}
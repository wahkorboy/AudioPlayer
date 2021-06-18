package com.wahkor.audioplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService

class NotificationHelper {
    private val channelId="com.wahkor.audioplayer"
    private val channelName="NewSong"
    fun build(context:Context){
        val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

           Notification.Builder(context,channelId)
               .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
                .setContentTitle("this is title")
                .setContentText("this is text")
        }else{
                Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
                    .setContentTitle("this is title")
                    .setContentText("this it text")
        }
        notificationManager.notify(1234,builder.build())

    }
}
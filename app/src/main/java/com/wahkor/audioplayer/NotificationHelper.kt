package com.wahkor.audioplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper {
    private val channelId="mediaPlayer"
    private val channelName="NewSong"
    fun build(context:Context):Notification?{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val   channel= NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH)
            val manager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            val notification =NotificationCompat.Builder(context,channelId).also {
                it.setContentTitle("this is title")
                it.setContentText("this is text")
            }.build()
            return notification
        }
        return null

    }
}
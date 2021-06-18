package com.wahkor.audioplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.wahkor.audioplayer.Constants.CHANNEL_ID
import com.wahkor.audioplayer.model.Song

class NotificationHelper {
    fun build(context: Context) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, "AudioPlayer", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    fun showNotification(context: Context, song: Song) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
                .setContentTitle("Media Player")
                .setContentText(song.title)
        } else {
            Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
                .setContentTitle("Media Player")
                .setContentText(song.title)
        }
        notificationManager.notify(1234, builder.build())
    }
}
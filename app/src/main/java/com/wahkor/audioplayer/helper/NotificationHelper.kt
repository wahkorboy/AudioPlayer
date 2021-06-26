package com.wahkor.audioplayer.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import com.wahkor.audioplayer.helper.Constants.CHANNEL_ID

class NotificationHelper {
    fun notificationChannel(context: Context){
            // Create the NotificationChannel
            val name = CHANNEL_ID
            val descriptionText = "MediaPlayer"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
    }
}
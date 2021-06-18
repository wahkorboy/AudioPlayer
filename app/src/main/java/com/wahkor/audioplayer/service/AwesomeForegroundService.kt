package com.wahkor.audioplayer.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import com.wahkor.audioplayer.Constants.CHANNEL_ID
import com.wahkor.audioplayer.Constants.MUSIC_NOTIFICATION_ID
import com.wahkor.audioplayer.MainActivity
import com.wahkor.audioplayer.PlaylistManager
import com.wahkor.audioplayer.R


class AwesomeForegroundService : Service() {
    private lateinit var mediaPlayer:MediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showNotification() {
        val notificationIntent=Intent(this,MainActivity::class.java)
        val pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0)

        val notification= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
                .setContentTitle("Media Player")
                .setContentText("first song")
        } else {
            Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
                .setContentTitle("Media Player")
                .setContentText("first song")
        }
        startForeground(MUSIC_NOTIFICATION_ID,notification.build())
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val serviceChannel=NotificationChannel(CHANNEL_ID,"AwesomeForegroundService",
                NotificationManager.IMPORTANCE_HIGH)

            val manager=getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

}
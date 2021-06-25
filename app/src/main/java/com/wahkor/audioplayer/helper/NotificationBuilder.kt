package com.wahkor.audioplayer.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
//import androidx.media.app.NotificationCompat
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.helper.Constants.CHANNEL_ID
import com.wahkor.audioplayer.helper.Constants.COMMAND_NEXT
import com.wahkor.audioplayer.helper.Constants.COMMAND_PLAY
import com.wahkor.audioplayer.helper.Constants.COMMAND_PREV
import com.wahkor.audioplayer.service.AudioService

class NotificationBuilder {
    fun createNotificationChannel(context: Context): NotificationManager {
        val manager =context.getSystemService(NotificationManager::class.java)
            val serviceChannel= NotificationChannel(
                CHANNEL_ID,"Music Player",
                NotificationManager.IMPORTANCE_LOW)

            manager.createNotificationChannel(serviceChannel)

        return manager
    }
    private lateinit var pauseAction:Notification.Action
    private lateinit var prevAction:Notification.Action
    private lateinit var nextAction:Notification.Action
    private fun setButton(context: Context,intent: Intent,playButton:Int){
        pauseAction= Notification.Action.Builder(
            playButton,
            COMMAND_PLAY,
            PendingIntent.getBroadcast(context, 0, intent.also {
                it.action = COMMAND_PLAY
            }, PendingIntent.FLAG_UPDATE_CURRENT)
        ).build()
        prevAction= Notification.Action.Builder(
            R.drawable.ic_baseline_skip_previous_24,
            COMMAND_PREV,
            PendingIntent.getBroadcast(context, 0, intent.also {
                it.action = COMMAND_PREV
            }, PendingIntent.FLAG_UPDATE_CURRENT)
        ).build()
        nextAction= Notification.Action.Builder(
            R.drawable.ic_baseline_skip_next_24,
            COMMAND_NEXT,
            PendingIntent.getBroadcast(context, 0, intent.also {
                it.action = COMMAND_NEXT
            }, PendingIntent.FLAG_UPDATE_CURRENT)
        ).build()
    }
    fun runningBuilder(context: Context, mySession: MediaSession, intent:Intent): Notification {
        setButton(context,intent,R.drawable.ic_baseline_pause_24)
        return Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .setContentTitle("Track title")
            .setContentText("Artist - Album")
            .setStyle(Notification.MediaStyle().setMediaSession(mySession.sessionToken)
            )
            .addAction(prevAction)
            .addAction(pauseAction)
            .addAction(nextAction)
            .build()
    }

    fun pauseBuilder(
        context: Context,
        mySession: MediaSession,
        intent: Intent
    ): Notification {
        setButton(context,intent,R.drawable.ic_baseline_play_arrow_24)
        return Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .setContentTitle("Track title")
            .setContentText("Artist - Album")
            .setStyle(Notification.MediaStyle().setMediaSession(mySession.sessionToken)
            )
            .addAction(prevAction)
            .addAction(pauseAction)
            .addAction(nextAction)
            .build()
    }
}
package com.wahkor.audioplayer.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.receiver.NotificationReceiver

class MusicNotification {
    fun createNotificationChannel(context: Context):NotificationManager {
        val manager =context.getSystemService(NotificationManager::class.java)
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val serviceChannel= NotificationChannel(
                Constants.CHANNEL_ID,"Music Player",
                NotificationManager.IMPORTANCE_LOW)

            manager.createNotificationChannel(serviceChannel)
        }
        return manager
    }

    fun runningNotification(context: Context): Notification.Builder {

        val intent= Intent(context, NotificationReceiver::class.java)
        val build=if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, Constants.CHANNEL_ID)
        } else {
            Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
        }
        build
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .setContentTitle("Media Player")
            .setContentText("wahkor")
            .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
            .setOngoing(true)
            .addAction(Notification.Action(
                R.drawable.ic_baseline_skip_previous_24,"previous",
                PendingIntent.getBroadcast(context,0,intent.also {
                    it.action="prev"
                }, PendingIntent.FLAG_UPDATE_CURRENT)))
            .addAction(Notification.Action(
                R.drawable.ic_baseline_pause_24,"play",
                PendingIntent.getBroadcast(context,0,intent.also {
                    it.action="play"
                }, PendingIntent.FLAG_UPDATE_CURRENT)))
            .addAction(Notification.Action(
                R.drawable.ic_baseline_skip_next_24,"next",
                PendingIntent.getBroadcast(context,0,intent.also {
                    it.action="next"
                }, PendingIntent.FLAG_UPDATE_CURRENT)))
            .style = Notification.MediaStyle()
        return build
    }

    fun pauseNotification(context: Context): Notification.Builder{

        val intent= Intent(context, NotificationReceiver::class.java)
        val build=if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, Constants.CHANNEL_ID)
        } else {
            Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
        }
        build
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .setContentTitle("Media Player")
            .setContentText("wahkor")
            .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
            .setOngoing(true)
            .addAction(Notification.Action(
                R.drawable.ic_baseline_skip_previous_24,"previous",
                PendingIntent.getBroadcast(context,0,intent.also {
                    it.action="prev"
                }, PendingIntent.FLAG_UPDATE_CURRENT)))
            .addAction(Notification.Action(
                R.drawable.ic_baseline_play_arrow_24,"play",
                PendingIntent.getBroadcast(context,0,intent.also {
                    it.action="play"
                }, PendingIntent.FLAG_UPDATE_CURRENT)))
            .addAction(Notification.Action(
                R.drawable.ic_baseline_skip_next_24,"next",
                PendingIntent.getBroadcast(context,0,intent.also {
                    it.action="next"
                }, PendingIntent.FLAG_UPDATE_CURRENT)))
            .style = Notification.MediaStyle()
        return build
    }
}
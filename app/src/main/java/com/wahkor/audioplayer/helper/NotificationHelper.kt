package com.wahkor.audioplayer.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.session.MediaSession
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
//import androidx.media.app.NotificationCompat
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.helper.Constants.CHANNEL_ID

class NotificationHelper {
    fun createNotificationChannel(context: Context): NotificationManager {
        val manager =context.getSystemService(NotificationManager::class.java)
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val serviceChannel= NotificationChannel(
                CHANNEL_ID,"Music Player",
                NotificationManager.IMPORTANCE_LOW)

            manager.createNotificationChannel(serviceChannel)
        }
        return manager
    }
fun runningBuilder(context: Context,mySession:MediaSessionCompat):Notification{
    return NotificationCompat.Builder(context,CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
        .setContentTitle("Track title")
        .setContentText("Artist - Album")
    .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
        .setMediaSession(mySession.sessionToken))
        .build();

}
}
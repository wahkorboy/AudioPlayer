package com.wahkor.audioplayer.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wahkor.audioplayer.PlaylistManager
import com.wahkor.audioplayer.R


class AwesomeForegroundService : Service() {
    private lateinit var mediaPlayer:MediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        iniMusic()
    }

    private fun iniMusic() {
        val plm=PlaylistManager()
        plm.build(this)
        plm.getSong("current"){song, position ->
            mediaPlayer= MediaPlayer()
            if (song != null) {
                mediaPlayer.setDataSource(song.data)
                mediaPlayer.prepare()
            }
        }
    }
}
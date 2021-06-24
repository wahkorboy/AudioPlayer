package com.wahkor.audioplayer.receiver

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModelProvider
import com.wahkor.audioplayer.helper.Constants.COMMAND_NEXT
import com.wahkor.audioplayer.helper.Constants.COMMAND_PREV
import com.wahkor.audioplayer.service.AudioService
import com.wahkor.audioplayer.viewmodel.PlayerModel

class NotificationReceiver : BroadcastReceiver() {
    private lateinit var mediaBrowserCompat: MediaBrowserCompat
    private lateinit var mediaControllerCompat: MediaControllerCompat
    private lateinit var remote: MediaControllerCompat.TransportControls
    private  var btn:String?=null
    override fun onReceive(context: Context?, intent: Intent?) {
        val serviceComponentName = ComponentName(context!!, AudioService::class.java)
        mediaBrowserCompat =
            MediaBrowserCompat(context, serviceComponentName, mediaBrowserConnectionCallback, null)

        mediaBrowserCompat.connect()
        btn = intent?.action
    }

    private val mediaBrowserConnectionCallback: MediaBrowserCompat.ConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                mediaControllerCompat = MediaControllerCompat(
                    Application(),
                    mediaBrowserCompat.sessionToken
                )
                mediaControllerCompat.registerCallback(mediaControllerCompatCallback)
                remote = mediaControllerCompat.transportControls
                //remote.play()

                btn?.let {
                    when (it) {
                        "prev" -> {
                            remote.skipToPrevious()
                            remote.play()
                        }//audioService.controlCommand(COMMAND_PREV)
                        "play" -> {
                            remote.play()
                        }//audioService.playPauseBTN()
                        "next" -> {
                            remote.skipToNext()
                        }//audioService.controlCommand(COMMAND_NEXT)
                        else -> {
                        }
                    }

                }

            }
        }

    private val mediaControllerCompatCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
        }
}

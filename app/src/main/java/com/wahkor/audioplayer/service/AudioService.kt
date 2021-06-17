package com.wahkor.audioplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.service.media.MediaBrowserService

class AudioService : MediaBrowserService() {
    val mediaPlayer = MediaPlayer()
    private lateinit var mediaSession: MediaSession
    private lateinit var playbackState: PlaybackState.Builder

    inner class MediaSessionCallback(val context: Context) : MediaSession.Callback(),
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
        private var intentFilter: IntentFilter =
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        private var audioManager: AudioManager =
            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        private val audioBecomingNoisy = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                mediaPause()
            }

        }

        override fun onPlay() {
            super.onPlay()
            mediaPlay()
        }

        override fun onPause() {
            mediaPause()
            super.onPause()
        }

        override fun onStop() {
            super.onStop()
            mediaStop()
        }

        override fun onCompletion(mp: MediaPlayer?) {
            playbackState.setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_STOP)
            playbackState.setState(
                PlaybackState.STATE_STOPPED,
                mediaPlayer.currentPosition.toLong(), 1.0f, SystemClock.elapsedRealtime()
            )
            mediaSession.setPlaybackState(playbackState.build())
        }

        override fun onAudioFocusChange(focusChange: Int) {
            when(focusChange){
                AudioManager.AUDIOFOCUS_GAIN->mediaPlay()
                else -> mediaPause()
            }
        }
        fun mediaStop(){
            mediaSession.isActive=false
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
        }
        private fun mediaPlay() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val audioFocusRequest =
                    AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build()
                context.registerReceiver(audioBecomingNoisy, intentFilter)
                val requestAudioFocusResult = audioManager.requestAudioFocus(audioFocusRequest)
                if (requestAudioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mediaSession.isActive = true
                    playbackState.setActions(PlaybackState.ACTION_PAUSE or PlaybackState.ACTION_STOP)
                    playbackState.setState(
                        PlaybackState.STATE_PLAYING,
                        mediaPlayer.currentPosition.toLong(),
                        1.0f,
                        SystemClock.elapsedRealtime()
                    )
                    mediaSession.setPlaybackState(playbackState.build())
                    mediaPlayer.start()
                }
            }
        }
        private fun mediaPause() {
            mediaPlayer.pause()
            playbackState.setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_STOP)
            playbackState.setState(
                PlaybackState.STATE_PAUSED,
                mediaPlayer.currentPosition.toLong(), 1.0f, SystemClock.elapsedRealtime()
            )
            mediaSession.setPlaybackState(playbackState.build())
            audioManager.abandonAudioFocus(this)
            unregisterReceiver(audioBecomingNoisy)

        }

    }// end of MediaSessionCallback

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaSession = MediaSession(this, "AudioService")
        mediaSession.setCallback(MediaSessionCallback(this))
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
        playbackState = PlaybackState.Builder()
        mediaPlayer.setOnCompletionListener(MediaSessionCallback(this))
        return START_STICKY
    }
}

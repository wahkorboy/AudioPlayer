package com.wahkor.audioplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import com.wahkor.audioplayer.PlaylistManager
import com.wahkor.audioplayer.model.Song

const val STATE_PAUSE = 0
const val STATE_PLAYING = 1
const val STATE_STOP = -1

class AudioService : MediaBrowserService(), AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener {
    private lateinit var mediaSession: MediaSessionCompat
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
            val keyEvent = mediaButtonIntent.extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                lastClick= currentClick
                currentClick=System.currentTimeMillis()
                if(lastClick+ delayClick> currentClick){
                    nextSong()
                }else{
                    if (mediaState == STATE_PLAYING) onPause() else onPlay()
                }
            }
            return super.onMediaButtonEvent(mediaButtonIntent)
        }

        override fun onPause() {
            super.onPause()
            mediaPause()
        }

        override fun onPlay() {
            super.onPlay()
            mediaPlay()
        }
    }

    companion object {
        lateinit var playlistManager: PlaylistManager
        private val mediaPlayer = MediaPlayer()
        private var currentSong: Song? = null
        private var tableName: String? = null
        private var playlist = ArrayList<Song>()
        private var mediaState = 0
        private var mediaPosition = 0
        private var lastClick = 0L
        private var currentClick = 0L
        private const val delayClick = 100L
    }

    private val audioBecomingNoisy = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mediaPause()
        }
    }

    fun mediaStop() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaSession = MediaSessionCompat(this, "MediaPlayer")
        mediaSession.isActive = true
        mediaSession.setCallback(mediaSessionCallback)
        androidx.media.session.MediaButtonReceiver.handleIntent(mediaSession, intent)
        playlistManager = PlaylistManager().also { it.build(this) }
        val intentFilter =
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(audioBecomingNoisy, intentFilter)
        playlistManager.getSong("current") { song, position ->
            mediaPosition = position
            song?.let {
                mediaPrepare(song)
            }
            mediaPlayer.setOnCompletionListener(this)
            mediaPlayer.setVolume(1.0f, 1.0f)

        }
        return START_STICKY
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot("MediaPlayer", null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }

    override fun onAudioFocusChange(focusChange: Int) {
        TODO("Not yet implemented")
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextSong()
    }

    private fun nextSong() {
        playlistManager.getSong("next") { song, position ->
            mediaPosition = position
            song?.let { mediaPrepare(song); mediaPlay() }
        }
    }

    private fun mediaPrepare(song: Song) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(song.data)
        mediaPlayer.prepare()
        currentSong = song
        playlist = playlistManager.getPlaylist
        tableName = playlistManager.getTableName
    }

    private fun mediaPlay() {
        mediaPlayer.start()
        mediaState = STATE_PLAYING
    }

    private fun mediaPause() {
        mediaPlayer.pause()
        mediaState = STATE_PAUSE
    }

    fun controlCommand(query: String, callback: (playlistView: PlayListView) -> Unit) {
        playlistManager.getSong(query) { song, position ->
            mediaPosition = position
            song?.let {
                mediaPrepare(song)
                if (mediaState == STATE_PLAYING) {
                    mediaPlay()
                }
                callback(getPlaylistView)

            }

        }
    }

    fun playPauseBTN(): Int {
        if (mediaState == STATE_PAUSE) mediaPlay() else mediaPause()
        return mediaState
    }

    fun updatePlaylist(newList: ArrayList<Song>, callback: (ArrayList<Song>) -> Unit) {
        playlistManager.updatePlaylist(newList) { result ->
            callback(result)
        }
    }

    val getPlaylistView: PlayListView
        get() {
            return PlayListView(
                tableName, playlist, currentSong, mediaPosition, mediaPlayer.currentPosition,
                mediaState
            )
        }

    fun seekTo(seek: Int) {
        mediaPlayer.seekTo(seek)
    }
}

data class PlayListView(
    val tableName: String?,
    val playlist: ArrayList<Song>,
    var song: Song?,
    val position: Int,
    val currentPosition: Int,
    val mediaState: Int
)
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
import com.wahkor.audioplayer.PlaylistManager
import com.wahkor.audioplayer.model.Song

class AudioService : MediaBrowserService(), AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener {
    private lateinit var playlistManager:PlaylistManager
    companion object{
        private val mediaPlayer = MediaPlayer()
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
        playlistManager= PlaylistManager(this)
        val intentFilter: IntentFilter =
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(audioBecomingNoisy, intentFilter)
        val audioManager: AudioManager =
            getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return START_STICKY
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot("MediaPlayer",null)
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
       val song= playlistManager.getSong("next")
        song?.let { mediaPlay(song) }
    }


    private fun mediaPlay(song: Song) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(song.data)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    private fun mediaPause() {
        mediaPlayer.pause()
    }
    fun ControlCommand(query:String){
        val song=playlistManager.getSong(query)
        song?.let {
            mediaPlay(song)
        }
    }
}

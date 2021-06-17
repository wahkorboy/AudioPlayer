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
import com.wahkor.audioplayer.PlaylistManager
import com.wahkor.audioplayer.model.Song

class AudioService : MediaBrowserService(), AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener {
    private val STATE_PAUSE=0
    private val STATE_PLAYING=1
    companion object{
        lateinit var playlistManager:PlaylistManager
        private val mediaPlayer = MediaPlayer()
        private var currentSong:Song?=null
        private var tableName:String?=null
        private var playlist=ArrayList<Song>()
        private var mediaState=0
        private var mediaPosition=0
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
        playlistManager= PlaylistManager().also { it.build(this) }
        val intentFilter =
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(audioBecomingNoisy, intentFilter)
        val audioManager: AudioManager =
            getSystemService(Context.AUDIO_SERVICE) as AudioManager
        playlistManager.getSong("current"){song, position ->
            mediaPosition=position
            song?.let {
                mediaPrepare(song)}

        }
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
       playlistManager.getSong("next"){song, position ->
           mediaPosition=position
           song?.let {mediaPrepare(song); mediaPlay() }       }
    }

    private fun mediaPrepare(song:Song){
        mediaPlayer.reset()
        mediaPlayer.setDataSource(song.data)
        mediaPlayer.prepare()
        currentSong=song
        playlist=playlistManager.getPlaylist
        tableName= playlistManager.getTableName
    }
    private fun mediaPlay() {
        mediaPlayer.start()
        mediaState=STATE_PLAYING
    }

    private fun mediaPause() {
        mediaPlayer.pause()
        mediaState=STATE_PAUSE
    }
    fun controlCommand(query:String,callback:(song:Song,tableName:String,newList:ArrayList<Song>,newPosition:Int)->Unit){
        playlistManager.getSong(query){song, position ->
            mediaPosition=position
            song?.let {
                mediaPrepare(song)
                if(mediaState==STATE_PLAYING){
                    mediaPlay()
                }
                callback(song, tableName!!, playlistManager.getPlaylist,position)

        }

        }
    }

    fun playPauseBTN() {
        if(mediaState==STATE_PAUSE)mediaPlay() else mediaPause()
    }

    fun updatePlaylist(newList: ArrayList<Song>, callback:(ArrayList<Song>)->Unit) {
        playlistManager.updatePlaylist(newList){result -> callback(result)
        }
    }

    val getSongName:Song? get() = currentSong
    val getTableName:String? get() = tableName
    val getPlaylist: ArrayList<Song> get() = playlist
}

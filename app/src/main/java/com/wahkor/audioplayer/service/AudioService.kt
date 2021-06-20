package com.wahkor.audioplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.os.Build
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import androidx.lifecycle.MutableLiveData
import com.wahkor.audioplayer.helper.Constants
import com.wahkor.audioplayer.helper.Constants.COMMAND_PLAY
import com.wahkor.audioplayer.helper.Constants.STATE_PAUSE
import com.wahkor.audioplayer.helper.Constants.STATE_PLAYING
import com.wahkor.audioplayer.helper.Constants.STATE_STOP
import com.wahkor.audioplayer.helper.MusicNotification
import com.wahkor.audioplayer.helper.PlaylistManager
import com.wahkor.audioplayer.model.PlayerInfo
import com.wahkor.audioplayer.model.Song

class AudioService : MediaBrowserService(), AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener {
    val getCurrentPosition: Int get() = mediaPlayer.currentPosition
    val getPlayerInfo: MutableLiveData<PlayerInfo> get() = mPlayerInfo
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
        private var mediaState = 0
        private var mediaPosition = 0
        private var lastClick = 0L
        private var currentClick = 0L
        private const val delayClick = 100L
        private lateinit var manager: NotificationManager
        private lateinit var runningBuild:Notification.Builder
        private lateinit var pauseBuild:Notification.Builder
        var mPlayerInfo=MutableLiveData<PlayerInfo>()
    }

    private val audioBecomingNoisy = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mediaPause()
        }
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val serviceChannel= NotificationChannel(
                Constants.CHANNEL_ID,"AwesomeForegroundService",
                NotificationManager.IMPORTANCE_HIGH)

            manager=getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        manager= MusicNotification().createNotificationChannel(this)
        runningBuild= MusicNotification().runningNotification(this)
        pauseBuild= MusicNotification().pauseNotification(this)
        //startForeground(Constants.MUSIC_NOTIFICATION_ID, build.build())
        mediaSession = MediaSessionCompat(this, "MediaPlayer")
        mediaSession.isActive = true
        mediaSession.setCallback(mediaSessionCallback)
        androidx.media.session.MediaButtonReceiver.handleIntent(mediaSession, intent)
        playlistManager = PlaylistManager().also { it.build(this) }
        val audioBecomingNoisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(audioBecomingNoisy, audioBecomingNoisyFilter)
        playlistManager.controlCommand("current") { song, position ->
            mediaPosition = position
            song?.let {
                mediaPrepare(song)
            }
            mediaPlayer.setOnCompletionListener(this)


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
        when(focusChange){
            AudioManager.AUDIOFOCUS_GAIN->mediaPlay()
            AudioManager.AUDIOFOCUS_LOSS->mediaStop()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT->mediaPause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK-> mediaPlayer.setVolume(0.3f,0.3f)
        }
    }

    private fun mediaPrepare(song: Song) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(song.data)
        mediaPlayer.prepare()
        mPlayerInfo.value= playlistManager.getPlayerInfo(mediaState)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextSong()
    }

    private fun nextSong() {
        playlistManager.controlCommand("next") { song, position ->
            mediaPosition = position
            song?.let { mediaPrepare(song); mediaPlay() }
        }
    }

    private fun mediaPlay() {
        mediaPlayer.setVolume(1.0f,1.0f)
        mediaPlayer.start()
        mediaState = STATE_PLAYING
        runningBuild.setContentTitle(mPlayerInfo.value?.song?.title)
        runningBuild.setContentText(mPlayerInfo.value?.song?.artist)
        manager.notify(Constants.MUSIC_NOTIFICATION_ID, runningBuild.build())
        mPlayerInfo.value= playlistManager.getPlayerInfo(mediaState)
    }

    private fun mediaPause() {
        mediaPlayer.pause()
        mediaState = STATE_PAUSE
        pauseBuild.setContentTitle(mPlayerInfo.value?.song?.title)
        pauseBuild.setContentText(mPlayerInfo.value?.song?.artist)
        manager.notify(Constants.MUSIC_NOTIFICATION_ID, pauseBuild.build())
        mPlayerInfo.value= playlistManager.getPlayerInfo(mediaState)
    }

    private fun mediaStop() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
        mediaState= STATE_STOP
        mPlayerInfo.value= playlistManager.getPlayerInfo(mediaState)
    }
    fun controlCommand(query: String) {
        playlistManager.controlCommand(query) { song, position ->
            mediaPosition = position
            song?.let {
                mediaPrepare(song)
                if (mediaState == STATE_PLAYING) {
                    mediaPlay()
                }
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

    fun seekTo(seek: Int) {
        mediaPlayer.seekTo(seek)
    }

    fun changePlaylist(newTable: String) {
        mediaPause()
        playlistManager.changPlaylist(newTable)
        controlCommand(COMMAND_PLAY)

    }

}
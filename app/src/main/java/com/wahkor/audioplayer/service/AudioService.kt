package com.wahkor.audioplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.wahkor.audioplayer.*
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.receiver.NotificationReceiver
import kotlin.random.Random

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
        private lateinit var manager: NotificationManager
        private lateinit var build:Notification.Builder
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
        createNotificationChannel()
        build=showNotification()
        startForeground(Constants.MUSIC_NOTIFICATION_ID, build.build())
        mediaSession = MediaSessionCompat(this, "MediaPlayer")
        mediaSession.isActive = true
        mediaSession.setCallback(mediaSessionCallback)
        androidx.media.session.MediaButtonReceiver.handleIntent(mediaSession, intent)
        playlistManager = PlaylistManager().also { it.build(this) }
        val audioBecomingNoisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(audioBecomingNoisy, audioBecomingNoisyFilter)
        playlistManager.getSong("current") { song, position ->
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
        currentSong = song
        playlist = playlistManager.getPlaylist
        tableName = playlistManager.getTableName
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

    private fun mediaPlay() {
        mediaPlayer.setVolume(1.0f,1.0f)
        mediaPlayer.start()
        mediaState = STATE_PLAYING
        build.setContentTitle(currentSong?.title)
        build.setContentText(currentSong?.artist)
        manager.notify(Constants.MUSIC_NOTIFICATION_ID, build.build())
    }

    private fun showNotification():Notification.Builder {

        val intent=Intent(this,NotificationReceiver::class.java)
        val build=if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, Constants.CHANNEL_ID)
        } else {
            Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
        }
        build
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .setContentTitle("Media Player")
            .setContentText("wahkor")
            .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
            .setOngoing(true)
            .addAction(Notification.Action(R.drawable.ic_baseline_skip_previous_24,"previous",
                PendingIntent.getBroadcast(this,0,intent.also {
                    it.action="prev"
                },PendingIntent.FLAG_UPDATE_CURRENT)))
            .addAction(Notification.Action(R.drawable.ic_baseline_play_arrow_24,"play",
                PendingIntent.getBroadcast(this,0,intent.also {
                    it.action="play"
                },PendingIntent.FLAG_UPDATE_CURRENT)))
            .addAction(Notification.Action(R.drawable.ic_baseline_skip_next_24,"next",
                PendingIntent.getBroadcast(this,0,intent.also {
                    it.action="next"
                },PendingIntent.FLAG_UPDATE_CURRENT)))
            .style =Notification.MediaStyle()
        return build
    }

    private fun mediaPause() {
        mediaPlayer.pause()
        mediaState = STATE_PAUSE
    }

    private fun mediaStop() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
        mediaState= STATE_STOP
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
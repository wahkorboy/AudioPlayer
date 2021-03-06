package com.wahkor.audioplayer.service

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import android.widget.Toast
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.wahkor.audioplayer.helper.Constants.COMMAND_NEXT
import com.wahkor.audioplayer.helper.Constants.COMMAND_PLAY
import com.wahkor.audioplayer.helper.Constants.COMMAND_PREV
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.helper.MusicNotification
import com.wahkor.audioplayer.model.DBPlaylist
import com.wahkor.audioplayer.model.Song


class AudioService : MediaBrowserServiceCompat(), AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener {
    companion object {
        private var lastClick = 0L
        private var currentClick = 0L
        private const val delayClick = 100L
        private lateinit var manager: NotificationManager
        private lateinit var runningBuild: Notification.Builder
        private lateinit var pauseBuild: Notification.Builder
        private var currentPosition=0
        private var duration:Int=0
        private lateinit var dbPlaylist:DBPlaylist
        private var mediaState=PlaybackStateCompat.STATE_NONE
    }

    val getDuration: Int get() = duration
    val getMediaState:Int get() = mediaState
val getCurrentPosition:Int get() = currentPosition
    private var mediaSessionCompat: MediaSessionCompat? = null
    private var mediaPlayer: MediaPlayer? = null
    private val audioBecomingNoisy = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) mediaSessionCallback.onPause()
        }
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
            val keyEvent = mediaButtonIntent.extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                lastClick = currentClick
                currentClick = System.currentTimeMillis()
                if (lastClick + delayClick > currentClick) {
                    onPlayFromSearch(COMMAND_NEXT,null)
                    onPause()   // reset Playback State
                    onPlay()
                } else {
                    if(mediaPlayer!!.isPlaying){
                        onPause()
                    }else{
                        onPlay()
                    }
                }
            }
            return true
        }

        override fun onPause() {
            super.onPause()
            currentPosition= mediaPlayer?.currentPosition ?:0
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer!!.pause()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
                mediaState=PlaybackStateCompat.STATE_PAUSED
                //show notification here
            }
        }

        override fun onPlay() {
            super.onPlay()
            currentPosition=mediaPlayer?.currentPosition?:0
            duration=mediaPlayer?.duration?:0
            if (!successfullyRetrievedAudioFocus()) return
            mediaSessionCompat!!.isActive = true
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            mediaState=PlaybackStateCompat.STATE_PLAYING

            //show notification at here ......
            mediaPlayer!!.start()
        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            super.onCustomAction(action, extras)
            when(action){
                "currentPosition"->currentPosition= mediaPlayer?.currentPosition ?: 0
                "playlist" ->{
                    dbPlaylist=DBConnect().getDBPlaylist(this@AudioService)
                }
            }
        }
        override fun onSkipToNext() {
            super.onSkipToNext()
            onPlayFromSearch(COMMAND_NEXT,null)
        }

        override fun onSkipToQueueItem(id: Long) {
            super.onSkipToQueueItem(id)
            val dbPlaylist=DBConnect()
            dbPlaylist.skipToQueueItem(this@AudioService,id.toInt())
            onPlayFromSearch(COMMAND_PLAY,null)
        }
        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            onPlayFromSearch(COMMAND_PREV,null)
        }
        override fun onPlayFromSearch(query: String?, extras: Bundle?) {
            super.onPlayFromSearch(query, extras)
            val dbConnect = DBConnect()
            try {
                val rawData = dbConnect.controlCommand(this@AudioService,query!!)
                dbPlaylist=rawData
                try {
                    mediaPlayer?.setDataSource(rawData.song.data)
                } catch (e: IllegalStateException) {
                    mediaPlayer?.release()
                    initMediaPlayer()
                    mediaPlayer?.setDataSource(rawData.song.data)
                }
                initMediaSessionMetadata(rawData.song)
            } catch (e: Exception) {
                return
            }

            try {
                mediaPlayer?.prepare()
            } catch (e: Exception) {
            }

            //Work with extras here if you want

        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            mediaPlayer?.seekTo(pos.toInt())
        }
    }



    private fun initMediaSessionMetadata(song: Song) {
        val metadataBuilder = MediaMetadataCompat.Builder()
        //Notification icon in card
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(
                resources, R.drawable.sym_def_app_icon
            )
        )
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(
                resources, R.drawable.sym_def_app_icon
            )
        )

        //lock screen icon for pre lollipop
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(
                resources, R.drawable.ic_media_play
            )
        )
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.title)
        metadataBuilder.putString(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
            "Display Subtitle"
        )

        // Duration.
        // If duration isn't set, such as for live broadcasts, then the progress
        // indicator won't be shown on the seekbar.
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration) // 4

        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1)
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1)
        mediaSessionCompat?.setMetadata(metadataBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        manager = MusicNotification().createNotificationChannel(this)
        runningBuild = MusicNotification().runningNotification(this)
        pauseBuild = MusicNotification().pauseNotification(this)
        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent)



        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        initMediaPlayer()
        initMediaSession()
        initNoisyReceiver()
    }

    private fun initNoisyReceiver() {
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(audioBecomingNoisy, filter)
    }

    private fun initMediaSession() {

        val mediaButtonReceiver = ComponentName(
            applicationContext,
            MediaButtonReceiver::class.java
        )
        mediaSessionCompat =
            MediaSessionCompat(applicationContext, "Tag", mediaButtonReceiver, null)

        mediaSessionCompat!!.setCallback(mediaSessionCallback)
        mediaSessionCompat!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.setClass(this, MediaButtonReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0)
        mediaSessionCompat!!.setMediaButtonReceiver(pendingIntent)

        sessionToken = mediaSessionCompat!!.sessionToken
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setVolume(1.0f, 1.0f)
        mediaPlayer!!.setOnCompletionListener(this)
    }

    private fun setMediaPlaybackState(state: Int) {
        val playbackStateBuilder = PlaybackStateCompat.Builder()
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE)
        } else {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY)
        }
        playbackStateBuilder.setState(state, mediaPlayer!!.currentPosition.toLong(), 0f)
        mediaSessionCompat?.setPlaybackState(playbackStateBuilder.build())
    }

    private fun successfullyRetrievedAudioFocus(): Boolean {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_GAIN
    }


    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot("MediaPlayer", null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }


    override fun onAudioFocusChange(focusChange: Int) {
        if (mediaPlayer == null) return
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                mediaPlayer!!.start()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
                mediaState=PlaybackStateCompat.STATE_PLAYING
            }
            AudioManager.AUDIOFOCUS_LOSS -> if (mediaPlayer?.isPlaying == true) {
                mediaPlayer!!.stop()
                setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED)
                mediaState=PlaybackStateCompat.STATE_STOPPED
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer!!.pause()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
                mediaState=PlaybackStateCompat.STATE_PAUSED
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaPlayer!!.setVolume(0.3f, 0.3f)
        }
    }


    override fun onCompletion(mp: MediaPlayer?) {
        if( mediaPlayer != null ) {
            mediaSessionCallback.onPause()
            mediaSessionCallback.onSkipToNext()
            mediaSessionCallback.onPlay()
           // mediaPlayer!!.release();
           // setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
           // mediaState=PlaybackStateCompat.STATE_SKIPPING_TO_NEXT
        }
    }

}
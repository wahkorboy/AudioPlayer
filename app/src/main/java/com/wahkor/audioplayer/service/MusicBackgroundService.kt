package com.wahkor.audioplayer.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.view.KeyEvent
import android.widget.Toast
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.helper.Constants
import com.wahkor.audioplayer.helper.Constants.MUSIC_NOTIFICATION_ID
import com.wahkor.audioplayer.helper.Constants.actionFastForward
import com.wahkor.audioplayer.helper.Constants.actionNext
import com.wahkor.audioplayer.helper.Constants.actionPause
import com.wahkor.audioplayer.helper.Constants.actionPlay
import com.wahkor.audioplayer.helper.Constants.actionPlayOrPause
import com.wahkor.audioplayer.helper.Constants.actionPrevious
import com.wahkor.audioplayer.helper.Constants.actionRewind
import com.wahkor.audioplayer.helper.Constants.actionStop
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.model.Song


class MusicBackgroundService : MediaBrowserServiceCompat(), AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener {
    override fun onBind(intent: Intent?): IBinder {
        return myBinder
    }

    var song: Song?=null
    companion object {
        private var lastClick = 0L
        private var currentClick = 0L
        private const val delayClick = 100L
    }
    private val myBinder=MyBinder()

    inner class MyBinder : Binder() {
        val service: MusicBackgroundService
            get() = this@MusicBackgroundService
    }
    private lateinit var mediaSession: MediaSession
    private lateinit var mediaController: MediaController
    private var notificationManager:NotificationManager?=null
    val mediaPlayer=MediaPlayer()
    private val audioBecomingNoisy = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (mediaPlayer.isPlaying) mediaController.transportControls.pause()
        }
    }

    private fun initMediaSessionMetadata() {
        //val metadataBuilder = MediaMetadataCompat.Builder()
        mediaSession.setMetadata(
            MediaMetadata.Builder().also {
                val currentTrack=song!!
                it.putString(MediaMetadata.METADATA_KEY_TITLE, currentTrack.title)
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, currentTrack.artist)
                    .putLong(MediaMetadata.METADATA_KEY_DURATION, currentTrack.duration) // 4

            }.build()
        )
        mediaSession.setPlaybackState(
            PlaybackState.Builder()
                .setState(
                    PlaybackState.STATE_PLAYING,
                    mediaPlayer.currentPosition.toLong(),
                    1f
                )
                .setActions(PlaybackState.ACTION_SEEK_TO)
                .build()
        )
    }
    private fun handleIntent(intent: Intent?){
        Toast.makeText(applicationContext, song?.title,Toast.LENGTH_LONG).show()
        if (intent==null || intent.action==null) return
        when(intent.action!!){
            actionPlayOrPause->{
                if (mediaPlayer.currentPosition==0){
                    mediaSessionCallback.onPlayFromSearch(actionPlay,null)
                    mediaController.transportControls.play()
                }else{
                    if(mediaPlayer.isPlaying)
                        mediaController.transportControls.pause() else
                        mediaController.transportControls.play()                }
            }
            actionPlay->{
                mediaController.transportControls.play()
            }
            actionPause->{
                mediaController.transportControls.pause()
            }
            actionStop->{
                mediaController.transportControls.stop()
            }
            actionFastForward->{
                mediaController.transportControls.fastForward()
            }
            actionNext->{
                mediaController.transportControls.skipToNext()
            }
            actionRewind->{
                mediaController.transportControls.rewind()
            }
            actionPrevious->{
                mediaController.transportControls.skipToPrevious()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action ==null && song !=null){
            return super.onStartCommand(intent, flags, startId)
        }
        if (song==null){
            mediaSessionCallback.onPlayFromSearch(actionPlay,null)
            initMediaPlayer()
            initMediaSession()
            initNoisyReceiver()
        }
        handleIntent(intent)
        return super.onStartCommand(intent, flags, startId)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaSession =
                MediaSession(applicationContext, "MediaPlayer", null)
        }

        mediaSession.setCallback(mediaSessionCallback)
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
        )

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.setClass(this, MediaButtonReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0)
        mediaSession.setMediaButtonReceiver(pendingIntent)


      //  sessionToken = mediaSessionCompat.sessionToken
        mediaController= MediaController(applicationContext,mediaSession.sessionToken)
    }

    private fun initMediaPlayer() {
        mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setVolume(1.0f, 1.0f)
        mediaPlayer.setOnCompletionListener(this)
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
        if (notificationManager== null) return
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                mediaController.transportControls.play()
            }
            AudioManager.AUDIOFOCUS_LOSS -> if (mediaPlayer.isPlaying) {
                mediaController.transportControls.stop()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaController.transportControls.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaPlayer.setVolume(0.3f, 0.3f)
        }
    }

    private fun generateAction(icon:Int,title:String,intentAction:String): Notification.Action{
        val intent=Intent(applicationContext,MusicBackgroundService::class.java)
        intent.action=intentAction
        val pendingIntent=PendingIntent.getService(applicationContext,1,intent,0)
        return Notification.Action.Builder(icon,title,pendingIntent).build()
    }
    private fun buildNotification(action: Notification.Action){
        iniMediaSession()
        mediaSession.setMetadata(setMetadata())
        setPlaybackState()
        val style=Notification.MediaStyle()
        style.setMediaSession(mediaSession.sessionToken)
        style.setMediaSession(mediaSession.sessionToken)
        val intent=Intent(applicationContext,MusicBackgroundService::class.java)
        intent.action=actionStop
        val pendingIntent=PendingIntent.getService(applicationContext,1,intent,0)
        val builder= Notification.Builder(this, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .setDeleteIntent(pendingIntent)
            .setStyle(style)
            .addAction(generateAction(R.drawable.ic_baseline_skip_previous_24,"previous",actionPrevious))
            .addAction(action)
            .addAction(generateAction(R.drawable.ic_baseline_skip_next_24,"next",actionNext))

         notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.notify(MUSIC_NOTIFICATION_ID,builder.build())
    }
    private fun iniMediaSession() {
        mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mediaSession= MediaSession(applicationContext,"Test LockScreen")
        mediaController= MediaController(applicationContext,mediaSession.sessionToken)
        mediaController
        mediaSession.setCallback(mediaSessionCallback)
    }
    private fun setPlaybackState(){
        mediaSession.setPlaybackState(
            PlaybackState.Builder()
                .setState(
                    PlaybackState.STATE_PLAYING,
                    mediaPlayer.currentPosition.toLong(),
                    1f
                )
                .setActions(PlaybackState.ACTION_SEEK_TO)
                .build()
        )
    }
    private fun setMetadata():MediaMetadata{

        return MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, song?.title)
            .putString(MediaMetadata.METADATA_KEY_ARTIST, song?.artist)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, song?.duration?:0) // 4
            .build()

    }
    override fun onCompletion(mp: MediaPlayer?) {
        if( notificationManager!= null ) {
            mediaController.transportControls.skipToNext()
        }
    }
    private val mediaSessionCallback=object : MediaSession.Callback(){
        override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
            val keyEvent = mediaButtonIntent.extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                lastClick = currentClick
                currentClick = System.currentTimeMillis()
                if (lastClick + delayClick > currentClick) {
                    onPlayFromSearch(actionNext,null)
                    onPause()   // reset Playback State
                    onPlay()
                } else {
                    if(mediaPlayer.isPlaying){
                        onPause()
                    }else{
                        onPlay()
                    }
                }
            }
            return true
        }
        override fun onPlay() {
            super.onPlay()
            mediaPlayer.setWakeMode(this@MusicBackgroundService, PowerManager.PARTIAL_WAKE_LOCK)
            buildNotification(generateAction(R.drawable.ic_baseline_pause_24,"Pause",actionPause))
            mediaPlayer.start()
        }

        override fun onPause() {
            super.onPause()
            mediaPlayer.setWakeMode(this@MusicBackgroundService, PowerManager.PARTIAL_WAKE_LOCK)
            buildNotification(generateAction(R.drawable.ic_baseline_play_arrow_24,"Play",actionPlay))
            mediaPlayer.pause()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            onPlayFromSearch(actionNext,null)
            onPlay()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            onPlayFromSearch(actionPrevious,null)
            onPlay()
        }

        override fun onStop() {
            super.onStop()
            val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(MUSIC_NOTIFICATION_ID)
            val intent=Intent(applicationContext,MusicBackgroundService::class.java)
            stopService(intent)
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            mediaPlayer.seekTo(pos.toInt())
        }
        override fun onPlayFromSearch(query: String?, extras: Bundle?) {
            super.onPlayFromSearch(query, extras)
            val dbPlaylist=DBConnect().controlCommand(this@MusicBackgroundService,query?: actionPlay)
            song=dbPlaylist.song
            mediaPlayer.reset()
            mediaPlayer.setDataSource(song!!.data)
            mediaPlayer.prepare()
            initMediaSessionMetadata()
            mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }
}

package com.wahkor.audioplayer.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.model.PlayerState
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import java.lang.Runnable
import kotlin.random.Random

class PlayerModel : ViewModel() {
    val change=MutableLiveData<CharSequence>()
    val toast=MutableLiveData<String>()
    private val handler= Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    // set observer and UI
    var songTitle: CharSequence = ""
    var playBTN = R.drawable.ic_baseline_play_arrow_24
val playerState=MutableLiveData<PlayerState>()
    //
    var runID = 0
    var current=0
    var duration=0
// remote command


    fun build(context: Context) {
        val serviceComponentName = ComponentName(context, AudioService::class.java)
        mediaBrowserCompat =
            MediaBrowserCompat(context, serviceComponentName, mediaBrowserConnectionCallback, null)
        mediaBrowserCompat.connect()
        //remote.play()
    }

fun setupRunnable(){
    val id= Random.nextInt(1,99999999)
    runID=id
    runnable=Runnable {
        current += 1000
        val tvPass=millSecToString(current)
        val tvDue=millSecToString(duration-current)
        playerState.value= PlayerState(songTitle,duration,current,tvPass,tvDue,playBTN)
        if (id!=runID) {
            return@Runnable
        }else{
            handler.postDelayed(runnable,1000)
        }
    }
    handler.postDelayed(runnable, 1000)
}


    private fun millSecToString(millSecs: Int): String {
        var secs = millSecs / 1000
        var minute = secs / 60
        val hours = minute / 60
        minute -= hours * 60
        secs = secs - minute * 60 - hours * 60 * 60
        var text = if (hours == 0) "" else "$hours:"
        text += if (minute < 10) "0$minute:" else "$minute:"
        text += if (secs < 10) "0$secs" else "$secs"
        return text
    }
    @SuppressLint("UseCompatLoadingForDrawables")

    private val mediaControllerCompatCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
                songTitle = metadata!!.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
            }
            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                super.onPlaybackStateChanged(state)
                when (state.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        duration=AudioService().getDuration
                        current=AudioService().getCurrentPosition
                        mediaState = PlaybackState.STATE_PLAYING
                        change.value=songTitle
                        setupRunnable()
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        mediaState = PlaybackState.STATE_PAUSED
                        runID = 0
                    }
                    PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                        mediaState = PlaybackState.STATE_SKIPPING_TO_NEXT
                        runID = 0
                        mediaControllerCompat.transportControls.skipToNext()
                    }
                    else -> {
                    }
                }
                playBTN = setPlayBTNImage()
            }
        }
    private val mediaBrowserConnectionCallback: MediaBrowserCompat.ConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                try {
                    mediaControllerCompat = MediaControllerCompat(
                        Application(),
                        mediaBrowserCompat.sessionToken
                    )
                    mediaControllerCompat.registerCallback(mediaControllerCompatCallback)
                    remote = mediaControllerCompat.transportControls
                    if (mediaState != PlaybackState.STATE_PLAYING && mediaState != PlaybackState.STATE_PAUSED) {
                        remote.playFromSearch("", null)
                        //remote.play()
                    }
                } catch (e: Exception) {
                }
            }
        }
    private fun setPlayBTNImage(): Int {
        return if (mediaState == PlaybackState.STATE_PLAYING)
            R.drawable.ic_baseline_pause_24
        else
            R.drawable.ic_baseline_play_arrow_24
    }

    private var mediaState: Int = AudioService().getMediaState
    private lateinit var mediaBrowserCompat: MediaBrowserCompat
    private lateinit var mediaControllerCompat: MediaControllerCompat
    private lateinit var remote: MediaControllerCompat.TransportControls

    fun prevClick() {
        val playing = mediaState == PlaybackStateCompat.STATE_PLAYING
        remote.skipToPrevious()
        if (playing) remote.play()
    }

    fun nextClick() {
        val playing = mediaState == PlaybackStateCompat.STATE_PLAYING
        remote.skipToNext()
        if (playing) remote.play()
    }

    fun actionClick() {
        if (mediaState == PlaybackStateCompat.STATE_PLAYING) remote.pause()
        else remote.play()
    }

    fun playlistAction() {
                val playing = mediaState == PlaybackStateCompat.STATE_PLAYING
                remote.playFromSearch("",null)
                if (playing) remote.play()
    }

    fun seekbar(progress: Int) {
        remote.seekTo(progress.toLong())
        current=progress
    }
}
package com.wahkor.audioplayer.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.model.PlayerState
import com.wahkor.audioplayer.service.AudioService
import java.lang.Runnable
import kotlin.random.Random

class PlayerModel29 : ViewModel() {
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
    //var duration=0
// remote command


    fun build(context: Context) {
        val serviceComponentName = ComponentName(context, AudioService::class.java)
        mediaBrowserCompat =
            MediaBrowser(context, serviceComponentName, mediaBrowserConnectionCallback, null)

        mediaBrowserCompat.connect()
        //remote.play()
    }

    fun setupRunnable(){
        val id= Random.nextInt(1,99999999)
        runID=id
        val duration= mediaController.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)?.toInt()?:0
        runnable=Runnable {
            val current= mediaController.playbackState?.position?.toInt()?:0
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

    private val mediaControllerCallback: MediaController.Callback =
        object : MediaController.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadata?) {
                super.onMetadataChanged(metadata)
                songTitle = metadata?.getText(MediaMetadata.METADATA_KEY_TITLE)?:""
            }

            override fun onPlaybackStateChanged(state: PlaybackState?) {
                super.onPlaybackStateChanged(state)
                when (state?.state) {
                    PlaybackState.STATE_PLAYING -> {
                        //duration=AudioService().getDuration
                        mediaState = PlaybackState.STATE_PLAYING
                        change.value=songTitle
                        setupRunnable()
                    }
                    PlaybackState.STATE_PAUSED -> {
                        mediaState = PlaybackState.STATE_PAUSED
                        runID = 0
                    }
                    PlaybackState.STATE_SKIPPING_TO_NEXT -> {
                        mediaState = PlaybackState.STATE_SKIPPING_TO_NEXT
                        runID = 0
                        mediaController.transportControls.skipToNext()
                    }
                    else -> {
                    }
                }
                playBTN = setPlayBTNImage()
            }
        }

    private val mediaBrowserConnectionCallback: MediaBrowser.ConnectionCallback =
        object : MediaBrowser.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                try {
                    mediaController = MediaController(
                        Application(),
                        mediaBrowserCompat.sessionToken
                    )
                    mediaController.registerCallback(mediaControllerCallback)
                    remote = mediaController.transportControls
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
    private lateinit var mediaBrowserCompat: MediaBrowser
    private lateinit var mediaController: MediaController
    private lateinit var remote: MediaController.TransportControls

    fun prevClick() {
        val playing = mediaState == PlaybackState.STATE_PLAYING
        remote.skipToPrevious()
        if (playing) remote.play()
    }

    fun nextClick() {
        val playing = mediaState == PlaybackState.STATE_PLAYING
        remote.skipToNext()
        if (playing) remote.play()
    }

    fun actionClick() {
        if (mediaState == PlaybackState.STATE_PLAYING) remote.pause()
        else remote.play()
    }

    fun playlistAction() {
        val playing = mediaState == PlaybackState.STATE_PLAYING
        remote.playFromSearch("",null)
        if (playing) remote.play()
    }

    fun seekbar(progress: Int) {
        remote.seekTo(progress.toLong())
    }
}
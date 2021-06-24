package com.wahkor.audioplayer.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaMetadata
import android.media.MediaMetadata.METADATA_KEY_DURATION
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.helper.Constants
import com.wahkor.audioplayer.helper.Constants.COMMAND_PLAY
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.model.DBPlaylist
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.random.Random
data class PlayerState(
    var duration:Int,
    var current:Int,
    var tvPass:String,
    val tvDue:String,
    val playBTN:Int
)
class PlayerModel : ViewModel() {
    private val handler= Handler(Looper.getMainLooper())
    private val runnable: Runnable by lazy {
        var id = Random.nextInt(1, 9999999)
        runID=id
        var duration=AudioService().getDuration
        current=AudioService().getCurrentPosition
        Runnable {
            current += 1000
            val tvPass=millSecToString(current)
            val tvDue=millSecToString(duration-current)
            playerState.value= PlayerState(duration,current,tvPass,tvDue,playBTN)
            if (id!=runID) {
                id = Random.nextInt(1, 9999999)
                runID=id
                duration=AudioService().getDuration
                current=AudioService().getCurrentPosition
            }else{
                handler.postDelayed(runnable,1000)
            }
        }
    }
    // set observer and UI
    var songTitle: CharSequence = ""
    var playBTN = R.drawable.ic_baseline_play_arrow_24
val playerState=MutableLiveData<PlayerState>()
    //
    var runID = 0
    var current=0
// remote command


    fun build(context: Context) {
        val serviceComponentName = ComponentName(context, AudioService::class.java)
        mediaBrowserCompat =
            MediaBrowserCompat(context, serviceComponentName, mediaBrowserConnectionCallback, null)
        mediaBrowserCompat.connect()
        //remote.play()
    }


    fun millSecToString(millSecs: Int): String {
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
                        mediaState = PlaybackState.STATE_PLAYING
                        handler.postDelayed(runnable,1000)
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

    private fun toast(context: Context, s: Any) {
        Toast.makeText(context, s.toString(), Toast.LENGTH_SHORT).show()
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

    fun playlistAction(context: Context, newList: ArrayList<Song>, action: String, position: Int) {
        when (action) {
            "click" -> {
                val playing = mediaState == PlaybackStateCompat.STATE_PLAYING
                remote.skipToQueueItem(position.toLong())
                if (playing) remote.play()

            }
        }
    }

    fun seekbar(progress: Int) {
        remote.seekTo(progress.toLong())
        current=progress
    }
}
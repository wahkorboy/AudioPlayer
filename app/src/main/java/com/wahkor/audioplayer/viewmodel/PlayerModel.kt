package com.wahkor.audioplayer.viewmodel

import android.content.Context
import android.content.Intent
import android.media.session.MediaController
import android.media.session.MediaSession
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.helper.Constants.actionNext
import com.wahkor.audioplayer.helper.Constants.actionPlayOrPause
import com.wahkor.audioplayer.helper.Constants.actionPrevious
import com.wahkor.audioplayer.model.PlayerState
import com.wahkor.audioplayer.service.MusicBackgroundService
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
    //var duration=0
// remote command


    private lateinit var intent: Intent
    fun build(context: Context) {
        intent= Intent(context,MusicBackgroundService::class.java)
        intent.action= actionPrevious
        context.startService(intent)

        //remote.play()
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun test(context: Context){
        val mediaSession=  MediaSession(context,"MediaPlayer",null)
        val mediaController=MediaController(context,mediaSession.sessionToken)
        mediaController.transportControls.play()


    }

fun setupRunnable(){
    val id= Random.nextInt(1,99999999)
    runID=id
    val duration=0
    runnable=Runnable {
        val current=0
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



    fun nextClick(context: Context) {
        intent.action= actionNext
        context.startService(intent)

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun actionClick(context: Context) {
        test(context)
        //intent.action= actionPlayOrPause
        //context.startService(intent)

    }fun prevClick(context: Context) {
        intent.action= actionPrevious
        context.startService(intent)

    }

    fun playlistAction() {
    }

    fun seekbar(progress: Int) {
        PlaybackStateCompat.ACTION_PAUSE
    }
}
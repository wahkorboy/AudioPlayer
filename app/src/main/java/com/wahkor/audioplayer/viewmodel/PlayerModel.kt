package com.wahkor.audioplayer.viewmodel

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.helper.Constants.actionNext
import com.wahkor.audioplayer.helper.Constants.actionPrevious
import com.wahkor.audioplayer.model.PlayerState
import com.wahkor.audioplayer.service.MusicService
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
        intent= Intent(context,MusicService::class.java)
        context.startService(intent)

        //remote.play()
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



    fun nextClick(context: Context) {
        intent.action= actionNext
        context.startService(intent)

    }
    fun actionClick(context: Context) {
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
    @SuppressLint("StaticFieldLeak")
    private lateinit var musicService: MusicService
    private var mServiceBound = false
    private val serviceConnect=object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val myBinder =  service as MusicService.MyBinder
            musicService= myBinder.service
            mServiceBound = true;

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mServiceBound=false
        }

    }
}
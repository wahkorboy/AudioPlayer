package com.wahkor.audioplayer.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.Constants.STATE_PAUSE
import com.wahkor.audioplayer.Constants.STATE_PLAYING
import com.wahkor.audioplayer.Constants.STATE_STOP
import com.wahkor.audioplayer.model.PlayerInfo
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import kotlinx.coroutines.*

class PlayerActivityModel:ViewModel(){
    val currentPosition=MutableLiveData<String>()
    val playlist=MutableLiveData<ArrayList<Song>>()
    val duration=MutableLiveData<String>()
    private lateinit var song:Song
   @SuppressLint("StaticFieldLeak")
   private val audioService= AudioService()

    private val modelJob= SupervisorJob()
    private val mainScope=CoroutineScope(Dispatchers.Main + modelJob)
    private var mediaState:Int= STATE_PAUSE

    private fun updateSeekbar(){
        mainScope.launch {
            var isPlaying=true
            while (isPlaying){
                delay(1000)
                when(mediaState){
                    STATE_PLAYING ->{
                        val current=audioService.getCurrentPosition
                        currentPosition.value=millSecToString(current)
                        duration.value=millSecToString(song.duration.toInt()-current)
                    }
                    STATE_PAUSE ->{
                        isPlaying=false

                    }
                    STATE_STOP ->{
                        isPlaying=false
                        currentPosition.value=millSecToString(0)
                        duration.value=millSecToString(0)
                    }
                }
            } }
    }
    fun setSongInfo(playerInfo:PlayerInfo?=null){
        val info=playerInfo?:audioService.getPlayerInfo.value!!
        song=info.song
        playlist.value=info.playlist
        mediaState=info.mediaState
        updateSeekbar()
    }
    private fun millSecToString(millSecs:Int):String{
        var secs=millSecs/1000
        var minute=secs/60
        val hours=minute/60
        minute -= hours * 60
        secs=secs-minute*60-hours*60*60
        var text=if (hours==0)"" else "$hours:"
        text+=if(minute<10)"0$minute:" else "$minute:"
        text+=if(secs<10)"0$secs" else "$secs"
        return text
    }

}
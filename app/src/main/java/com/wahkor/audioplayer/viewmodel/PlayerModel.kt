package com.wahkor.audioplayer.viewmodel

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.MediaMetadata.METADATA_KEY_DURATION
import android.media.session.PlaybackState
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.model.DBPlaylist
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import kotlinx.coroutines.*
import kotlin.random.Random

class PlayerModel :ViewModel(){

    private var mCurrentState= PlaybackState.STATE_PAUSED
    private lateinit var mMediaBrowserCompat: MediaBrowserCompat
    private lateinit var mMediaControllerCompat: MediaControllerCompat
    lateinit var remote: MediaControllerCompat.TransportControls
    private var intDuration=0
    val progress=MutableLiveData<Int>()
    val duration=MutableLiveData<String>()
    val currentPosition=MutableLiveData<String>()
    var name:CharSequence=""
    val toast=MutableLiveData<String>()
    val playlist=MutableLiveData<ArrayList<Song>>()
    private val mMediaControllerCompatCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
                name=metadata!!.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                intDuration= metadata.getLong(METADATA_KEY_DURATION).toInt()
            }


            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                super.onPlaybackStateChanged(state)
                when (state.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        mCurrentState = PlaybackState.STATE_PLAYING
                        updateSeekbar()
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        mCurrentState = PlaybackState.STATE_PAUSED
                        runid=0
                    }
                    PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ->{
                        mCurrentState= PlaybackState.STATE_SKIPPING_TO_NEXT
                        runid=0
                        remote.skipToNext()
                    }
                    else ->{}
                }
            }
        }
    private val mediaBrowserConnectionCallback:MediaBrowserCompat.ConnectionCallback=
        object:MediaBrowserCompat.ConnectionCallback(){
            override fun onConnected() {
                super.onConnected()
                try {
                    remote=mMediaControllerCompat.transportControls
                    mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback)
                } catch (e: Exception) {
                }
            }
        }

    private var runid=0
    private fun updateSeekbar(){
        val modelJob= SupervisorJob()
        val mainScope= CoroutineScope(Dispatchers.Main + modelJob)
        val id= Random.nextInt(1,9999999)
        runid=id
        remote.sendCustomAction("currentPosition",null)
        var current=AudioService().getCurrentPosition

        mainScope.launch {
            while(runid==id){
                delay(1000)
                current+=1000
                progress.value=current
            }
        }
    }
    fun build(context: Context){
        val serviceComponentName= ComponentName(context, AudioService::class.java)
        mMediaBrowserCompat= MediaBrowserCompat(context,serviceComponentName,mediaBrowserConnectionCallback,null)
        mMediaControllerCompat = MediaControllerCompat(
            context,
            mMediaBrowserCompat.sessionToken
        )
        mMediaBrowserCompat.connect()
    }

    fun recyclerCallback(newList: java.util.ArrayList<Song>, action: String, position: Int): Any {
        TODO("Not yet implemented")
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

    fun setSongInfo() {
        TODO("Not yet implemented")
    }
}
package com.wahkor.audioplayer.viewmodel

import android.app.Application
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
import com.wahkor.audioplayer.helper.Constants
import com.wahkor.audioplayer.helper.Constants.COMMAND_PLAY
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import kotlinx.coroutines.*
import kotlin.random.Random

class PlayerModel :ViewModel(){

    private var mCurrentState= PlaybackState.STATE_PAUSED
    private var mediaState:Int=AudioService().getMediaState
    private lateinit var mMediaBrowserCompat: MediaBrowserCompat
    private lateinit var mMediaControllerCompat: MediaControllerCompat
    private val modelJob= SupervisorJob()
    private val mainScope=CoroutineScope(Dispatchers.Main + modelJob)
    private lateinit var song:Song
    lateinit var tableName:String
    lateinit var remote: MediaControllerCompat.TransportControls
    private var intDuration=0

    val progress=MutableLiveData<Int>()
    val duration=MutableLiveData<String>()
    val currentPosition=MutableLiveData<String>()
    val name=MutableLiveData<CharSequence>()
    val toast=MutableLiveData<String>()
    val playlist=MutableLiveData<ArrayList<Song>>()

    private val mMediaControllerCompatCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
                name.value=metadata!!.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
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
                        runID=0
                    }
                    PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ->{
                        mCurrentState= PlaybackState.STATE_SKIPPING_TO_NEXT
                        runID=0
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
            }
        }

    private var runID=0
    private fun updateSeekbar(){
        val modelJob= SupervisorJob()
        val mainScope= CoroutineScope(Dispatchers.Main + modelJob)
        val id= Random.nextInt(1,9999999)
        runID=id
        remote.sendCustomAction("currentPosition",null)
        var current=AudioService().getCurrentPosition

        mainScope.launch {
            while(runID==id){
                delay(1000)
                current+=1000
                progress.value=current
            }
        }
    }
    fun build(context: Context){
        val serviceComponentName= ComponentName(context, AudioService::class.java)
        mMediaBrowserCompat= MediaBrowserCompat(context,serviceComponentName,mediaBrowserConnectionCallback,null)

        mMediaBrowserCompat.connect().also {
            try {mMediaControllerCompat = MediaControllerCompat(
                context,
                mMediaBrowserCompat.sessionToken
            )
                remote=mMediaControllerCompat.transportControls
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback)
            } catch (e: Exception) {  }
        }
    }

    fun recyclerCallback(context: Context,newList:ArrayList<Song>, action: String, position: Int) {
        // audioService.updatePlaylist(newList){}
        when(action){
            Constants.ITEM_CLICK ->{
                // audioService.updatePlaylist(newList){}
                if(newList[position].data!=song.data){
                   remote.skipToQueueItem(position.toLong())
                }
            }

            Constants.ITEM_MOVE ->{
                DBConnect().updatePlaylist(context,newList,tableName)
            }

            Constants.ITEM_REMOVE ->{
                if (tableName != "playlist_default"){
                    //audioService.updatePlaylist(newList){}
                    if(newList.size==0){
                        // audioService.changePlaylist("playlist_default")
                    }
                }else{
                    val old=playlist.value
                    playlist.value=newList
                    mainScope.launch {
                        delay(100)
                        playlist.value=old!!
                    }

                }
            }
        }
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
        mediaState=AudioService().getMediaState
        when(mediaState){
            PlaybackStateCompat.STATE_PLAYING->{

            }
            else->{
                remote.playFromSearch(COMMAND_PLAY,null)
            }

        }
        remote.sendCustomAction("currentPosition",null)
        val dbPlaylist=AudioService().getDBPlaylist
        playlist.value=dbPlaylist.playlist
        intDuration=dbPlaylist.song.duration.toInt()
        name.value=dbPlaylist.song.title
        val current=AudioService().getCurrentPosition
        duration.value=millSecToString(intDuration-current)
        currentPosition.value=millSecToString(current)
        progress.value=current

        updateSeekbar()
    }
}
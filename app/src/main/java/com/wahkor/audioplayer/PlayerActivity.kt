package com.wahkor.audioplayer

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaMetadata
import android.media.session.PlaybackState
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.audioplayer.helper.Constants.STATE_PLAYING
import com.wahkor.audioplayer.`interface`.MenuInterface
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.databinding.ActivityPlayerBinding
import com.wahkor.audioplayer.helper.Constants.COMMAND_PLAY
import com.wahkor.audioplayer.helper.Constants.DEFAULT_PLAYLIST
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.model.DBPlaylist
import com.wahkor.audioplayer.service.AudioService
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.random.Random

class PlayerActivity : AppCompatActivity(),MenuInterface {
    private val binding:ActivityPlayerBinding by lazy { ActivityPlayerBinding.inflate(layoutInflater)}
    private lateinit var adapter:PlaylistAdapter
    private val audioPlaylist=MutableLiveData<DBPlaylist>()
    private var tableName= DEFAULT_PLAYLIST
    private var intDuration=0
    private val modelJob= SupervisorJob()
    private val mainScope= CoroutineScope(Dispatchers.Main + modelJob)
    private val audioService=AudioService()
    private val dbConnect=DBConnect()

    private var runID=0
    private var handler=Handler(Looper.getMainLooper())
    private lateinit var runnable:Runnable

    var remoteIsReady=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val serviceComponentName= ComponentName(this, AudioService::class.java)
        mediaBrowserCompat= MediaBrowserCompat(this,serviceComponentName,mediaBrowserConnectionCallback,null)
        mediaBrowserCompat.connect()

        initial()
        setButtonListener()
    }


    private fun setButtonListener() {
        binding.PlayerPlay.setOnClickListener {
            if(mediaState==PlaybackState.STATE_PLAYING)remote.pause()
            else remote.play()
        }
        binding.PlayerPrev.setOnClickListener {
           remote.skipToPrevious()
            remote.play()
             }
        binding.PlayerNext.setOnClickListener {
            remote.skipToNext()
            remote.play()
        }

        binding.PlayerSeekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    remote.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }


    private fun setPlayBTNImage():Drawable?{
    return if (mediaState== PlaybackState.STATE_PLAYING)
        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_pause_24, null)
    else
        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_play_arrow_24, null)
}

    override fun initial() {
        binding.PlayerMenu.setOnClickListener {
            setOnMenuClick(this, PopupMenu(this,binding.PlayerMenu),tableName){
                    intent ->  startActivity(intent)
            }
        }
        binding.PlayerSetting.setOnClickListener {
            setOnSettingClick(this, PopupMenu(this,binding.PlayerSetting)){
                    intent ->  startActivity(intent)
            }
        }
        binding.PlayerRecycler.layoutManager=LinearLayoutManager(this)


    }


    private val mediaControllerCompatCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
                binding.PlayerTitle.text=metadata!!.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
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
                        runID=0
                    }
                    PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ->{
                        mediaState= PlaybackState.STATE_SKIPPING_TO_NEXT
                        runID=0
                        mediaControllerCompat.transportControls.skipToNext()
                    }
                    else ->{}
                }
                binding.PlayerPlay.setImageDrawable(setPlayBTNImage())
            }
        }
    fun setRunnable(){
        val id= Random.nextInt(1,9999999)
        runID=id
        binding.PlayerSeekBar.max=dbConnect.getDBPlaylist(this).song.duration.toInt()
        runnable=java.lang.Runnable {
            while (runID==id) {
                Thread.sleep(1000)
                binding.PlayerSeekBar.progress=AudioService().getCurrentPosition
            }
            handler.postDelayed(runnable,1000)
        }
    }
    private val mediaBrowserConnectionCallback: MediaBrowserCompat.ConnectionCallback=
        object: MediaBrowserCompat.ConnectionCallback(){
            override fun onConnected() {
                super.onConnected()
                try {mediaControllerCompat = MediaControllerCompat(this@PlayerActivity,
                    mediaBrowserCompat.sessionToken
                )
                    mediaControllerCompat.registerCallback(mediaControllerCompatCallback)
                    remote=mediaControllerCompat.transportControls
                    remoteIsReady=true
                    if (mediaState!=PlaybackState.STATE_PLAYING && mediaState!=PlaybackState.STATE_PAUSED){
                        remote.playFromSearch("",null)
                    }
                } catch (e: Exception) {
                    binding.PlayerTitle.text=e.toString()
                }
            }
        }

    private fun toast(s: Any) {
        Toast.makeText(this,s.toString(),Toast.LENGTH_SHORT).show()
    }

    private var mediaState:Int=AudioService().getMediaState
    private lateinit var mediaBrowserCompat: MediaBrowserCompat
    private lateinit var mediaControllerCompat: MediaControllerCompat
    private lateinit var remote:MediaControllerCompat.TransportControls

}
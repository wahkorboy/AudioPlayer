package com.wahkor.audioplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.media.browse.MediaBrowser
import android.media.session.PlaybackState
import android.media.session.PlaybackState.*
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.wahkor.audioplayer.databinding.ActivityFeatureTestBinding
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.service.AudioService


class FeatureTestActivity : AppCompatActivity() {
    private val binding:ActivityFeatureTestBinding by lazy {
        ActivityFeatureTestBinding.inflate(layoutInflater)
    }
    private val db=DBConnect()
    private var mCurrentState= STATE_PAUSED
    private lateinit var mMediaBrowserCompat:MediaBrowserCompat
    private lateinit var mMediaControllerCompat:MediaControllerCompat
    private lateinit var remote:MediaControllerCompat.TransportControls
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val serviceComponentName= ComponentName(this,AudioService::class.java)
        mMediaBrowserCompat= MediaBrowserCompat(this@FeatureTestActivity,serviceComponentName,mediaBrowserConnectionCallback,null)
        mMediaBrowserCompat.connect()
        //mMediaControllerCompat.transportControls.playFromSearch("",null)

    }
    private fun toast(message:Any){
        Toast.makeText(this,message.toString(),Toast.LENGTH_SHORT).show()
    }
    private val mMediaControllerCompatCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
            }

            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                super.onPlaybackStateChanged(state)
                when (state.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        mCurrentState = STATE_PLAYING
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        mCurrentState = STATE_PAUSED
                    }
                    PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ->{
                        mCurrentState= STATE_SKIPPING_TO_NEXT
                        remote.skipToNext()
                    }
                    else ->{binding.testShowtext.text=state.state.toString()}
                }
            }
        }
    private val mediaBrowserConnectionCallback:MediaBrowserCompat.ConnectionCallback=
        object:MediaBrowserCompat.ConnectionCallback(){
            override fun onConnected() {
                super.onConnected()
                try {
                    mMediaControllerCompat = MediaControllerCompat(
                        this@FeatureTestActivity,
                        mMediaBrowserCompat.sessionToken
                    )
                    remote=mMediaControllerCompat.transportControls
                    mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback)

                    remote.playFromSearch("",null)

                } catch (e: Exception) {
                    binding.testShowtext.text=e.toString()
                }
            }
        }
    @SuppressLint("UseCompatLoadingForDrawables")
    fun actionBTN(view: View) {
        val btn=view as ImageView
        if(mCurrentState== STATE_PAUSED){
            mMediaControllerCompat.transportControls.play()
            btn.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_24))
        }else{
            mMediaControllerCompat.transportControls.pause()
            btn.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_arrow_24))
        }
    }
    fun PrevBTN(view: View) {
        remote.skipToPrevious()
        remote.play()
    }
    fun nextBTN(view: View) {
        remote.skipToNext()
        remote.play()
    }

}
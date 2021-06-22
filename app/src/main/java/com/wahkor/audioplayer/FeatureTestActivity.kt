package com.wahkor.audioplayer

import android.content.ComponentName
import android.media.browse.MediaBrowser
import android.media.session.PlaybackState.STATE_PAUSED
import android.media.session.PlaybackState.STATE_PLAYING
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wahkor.audioplayer.databinding.ActivityFeatureTestBinding
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.service.AudioService


class FeatureTestActivity : AppCompatActivity() {
    private val binding:ActivityFeatureTestBinding by lazy {
        ActivityFeatureTestBinding.inflate(layoutInflater)
    }
    private val db=DBConnect()
    private var mCurrentState=2
    private lateinit var mMediaBrowserCompat:MediaBrowserCompat
    private lateinit var mMediaControllerCompat:MediaControllerCompat
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

            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                super.onPlaybackStateChanged(state)
                when (state.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        mCurrentState = STATE_PLAYING
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        mCurrentState = STATE_PAUSED
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
                    mMediaControllerCompat = MediaControllerCompat(
                        this@FeatureTestActivity,
                        mMediaBrowserCompat.sessionToken
                    )
                    mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback)

                    mMediaControllerCompat.transportControls.playFromSearch("",null)

                } catch (e: Exception) {
                    binding.testShowtext.text=e.toString()
                }
            }
        }
var pnp=true
    fun actionBTN(view: View) {
        if(pnp){
            mMediaControllerCompat.transportControls.play()
        }else{
            //mMediaControllerCompat.transportControls.pause()

        }
        pnp=!pnp
    }

}
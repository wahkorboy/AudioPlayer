package com.wahkor.audioplayer

import android.media.browse.MediaBrowser
import android.os.Bundle
import android.media.session.PlaybackState.STATE_PAUSED
import android.media.session.PlaybackState.STATE_PLAYING
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wahkor.audioplayer.databinding.ActivityFeatureTestBinding
import com.wahkor.audioplayer.helper.DBConnect


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
        mediaBrowserConnectionCallback.onConnected()
        mCurrentState= mediaController.playbackState?.state!!
        toast(mCurrentState)

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
                toast(state.state)
            }
        }
    private val mediaBrowserConnectionCallback:MediaBrowser.ConnectionCallback=
        object:MediaBrowser.ConnectionCallback(){
            override fun onConnected() {
                super.onConnected()
                try {
                    mMediaControllerCompat = MediaControllerCompat(
                        this@FeatureTestActivity,
                        mMediaBrowserCompat.sessionToken
                    )
                    mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback)
                    mMediaControllerCompat.transportControls.play()

                } catch (e: Exception) {
                }
            }
        }

}
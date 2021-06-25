package com.wahkor.audioplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wahkor.audioplayer.databinding.ActivityFeatureTestBinding
import com.wahkor.audioplayer.helper.Constants.actionPlay
import com.wahkor.audioplayer.service.MusicBackgroundService


class FeatureTestActivity : AppCompatActivity(){
    private val binding: ActivityFeatureTestBinding by lazy {
        ActivityFeatureTestBinding.inflate(layoutInflater)
    }

    private lateinit var musicService: MusicBackgroundService
    private var mServiceBound = false
    private val serviceConnect=object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val myBinder =  service as MusicBackgroundService.MyBinder
            musicService= myBinder.getService
            mServiceBound = true;

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mServiceBound=false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
       musicService.mediaPlayer
    }

    override fun onStart() {
        super.onStart()
        val intent= Intent(this,MusicBackgroundService::class.java)
        intent.action=actionPlay
        startService(intent)
        bindService(intent,serviceConnect, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mServiceBound) {
            unbindService(serviceConnect);
            mServiceBound = false;
        }
    }

    private fun toast(message: Any) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }

    fun prevBTN(view: View) {

    }

    fun nextBTN(view: View) {
    }

    fun actionBTN(view: View) {
    }

}
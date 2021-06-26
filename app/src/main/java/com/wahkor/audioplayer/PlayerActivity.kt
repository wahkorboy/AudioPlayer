package com.wahkor.audioplayer

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.databinding.ActivityPlayerBinding
import com.wahkor.audioplayer.service.MusicService
import com.wahkor.audioplayer.viewmodel.PlayerModel


class PlayerActivity : AppCompatActivity() {
    private val handler=Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var adapter: PlaylistAdapter
    private lateinit var viewModel: PlayerModel
    private var scroll=false
    private lateinit var musicService:MusicService
    private var serviceBond=false
    private val binding: ActivityPlayerBinding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel =
            ViewModelProvider.AndroidViewModelFactory(Application()).create(PlayerModel::class.java)

        binding.PlayerRecycler.layoutManager = LinearLayoutManager(this)
        viewModel.toast.observe(this,{
            toast(it)
        })
        ini()
    }

    private fun ini() {
        runnable= Runnable {
            val duration=musicService.mediaPlayer.duration
            val progress=musicService.mediaPlayer.currentPosition
            val song=musicService.song
            binding.PlayerSeekBar.max=duration
            binding.PlayerSeekBar.progress=progress
            binding.PlayerTvPass.text=viewModel.millSecToString(progress)
            binding.PlayerTvDue.text=viewModel.millSecToString(duration-progress)
            binding.PlayerTitle.text= song?.title ?:""
            handler.postDelayed(runnable,1000 ) }
        binding.PlayerPlay.setOnClickListener {
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)
    }


    private fun toast(message: Any) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {

    }

    override fun onStart() {
        super.onStart()
        val intent= Intent(this,MusicService::class.java)
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)
        serviceBond=true

    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
        serviceBond=false

    }
    private val serviceConnection=object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val myBinder=service as MusicService.MyBinder
            musicService=myBinder.service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBond=false
        }

    }
}
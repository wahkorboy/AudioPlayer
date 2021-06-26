package com.wahkor.audioplayer

import android.app.Application
import android.app.PendingIntent.getActivity
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.databinding.ActivityPlayerBinding
import com.wahkor.audioplayer.helper.Constants.actionNext
import com.wahkor.audioplayer.helper.Constants.actionPlayOrPause
import com.wahkor.audioplayer.helper.Constants.actionPrevious
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.service.MusicService
import com.wahkor.audioplayer.viewmodel.PlayerModel


class PlayerActivity : AppCompatActivity() {
    private val handler=Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var adapter: PlaylistAdapter
    private lateinit var viewModel: PlayerModel
    private var scroll=true
    private var musicService:MusicService?=null
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
        setButtonListener()
        viewModel.updateUI(this,true)
            viewModel.dbPlaylist.observe(this,{
                adapter= PlaylistAdapter(it.playlist){ newList, action, position ->
                    viewModel.playListAction(this,newList,action,position)
                }
                binding.PlayerRecycler.adapter=adapter
                adapter.notifyDataSetChanged()
                if (scroll){
                    binding.PlayerRecycler.smoothScrollToPosition(it.position)
                    scroll=false
                }
            })
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            uiReceiver, IntentFilter("UPDATE_UI_PLAYER"));
    }

    private fun setButtonListener() {
        binding.PlayerPlay.setOnClickListener(
            viewModel.BTNListener(this,binding.PlayerPlay,actionPlayOrPause))
        binding.PlayerPrev.setOnClickListener(
            viewModel.BTNListener(this,binding.PlayerPlay,actionPrevious))
        binding.PlayerNext.setOnClickListener(
            viewModel.BTNListener(this,binding.PlayerPlay,actionNext))
    }

    private fun ini() {
        runnable= Runnable {

            val duration= musicService!!.mediaPlayer.duration
            val progress= musicService!!.mediaPlayer.currentPosition
            val song= musicService!!.song
            binding.PlayerSeekBar.max=duration
            binding.PlayerSeekBar.progress=progress
            binding.PlayerTvPass.text=viewModel.millSecToString(progress)
            binding.PlayerTvDue.text=viewModel.millSecToString(duration-progress)
            binding.PlayerTitle.text= song?.title ?:""
            viewModel.updatePlayBTN(this, musicService!!.mediaPlayer.isPlaying,binding.PlayerPlay)
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

    private val uiReceiver=object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            scroll=true
            viewModel.updateUI(this@PlayerActivity,true)

            }
        }

}
package com.wahkor.audioplayer

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.audioplayer.Constants.STATE_PLAYING
import com.wahkor.audioplayer.`interface`.MenuInterface
import com.wahkor.audioplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.databinding.ActivityPlayerBinding
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import com.wahkor.audioplayer.viewmodel.PlayerActivityModel

class PlayerActivity : AppCompatActivity(),MenuInterface {
    private val binding:ActivityPlayerBinding by lazy { ActivityPlayerBinding.inflate(layoutInflater)}
    private lateinit var adapter:PlaylistAdapter
    private val audioService=AudioService()

    private lateinit var viewModel:PlayerActivityModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.PlayerSetting.setOnClickListener {
            setOnSettingClick(this, PopupMenu(this,binding.PlayerSetting)){
                    intent ->  startActivity(intent)
            }
        }

        viewModel= ViewModelProvider.AndroidViewModelFactory(Application()).create(
            PlayerActivityModel::class.java)
        viewModel.setSongInfo()
        audioService.getPlayerInfo.observe(this,{
                playerInfo ->
            viewModel.setSongInfo(playerInfo)
        })

        adapter= PlaylistAdapter(viewModel.playlist.value!!){ newList, action ->
            audioService.updatePlaylist(newList){result ->
                adapter.notifyDataSetChanged()
                audioService.controlCommand("current")
            }
        }
        binding.PlayerRecycler.layoutManager=LinearLayoutManager(this)
        binding.PlayerRecycler.adapter=adapter
        val callback = CustomItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.PlayerRecycler)
        adapter.notifyDataSetChanged()

        audioService.getPlayerInfo.observe(this,{
            binding.PlayerSeekBar.max=it.song.duration.toInt()
            binding.playerPlaylistName.text=it.tableName
            binding.PlayerTitle.text=it.song.title
            adapter.notifyDataSetChanged()
            binding.PlayerRecycler.scrollToPosition(it.position)
            binding.PlayerPlay.setImageDrawable(setPlayBTNImage(it.mediaState))
        })
        setButtonListener()
    }

    private fun setButtonListener() {
        binding.PlayerPlay.setOnClickListener {
            audioService.playPauseBTN()
        }
        binding.PlayerPrev.setOnClickListener {
            audioService.controlCommand("prev") }
        binding.PlayerNext.setOnClickListener { audioService.controlCommand("next") }

        binding.PlayerSeekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    audioService.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }


    private fun setPlayBTNImage(mediaState:Int):Drawable?{
    return if (mediaState== STATE_PLAYING)
        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_pause_24, null)
    else
        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_play_arrow_24, null)
}

    }
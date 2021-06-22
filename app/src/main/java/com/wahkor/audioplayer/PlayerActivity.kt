package com.wahkor.audioplayer

import android.app.Application
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.audioplayer.helper.Constants.STATE_PLAYING
import com.wahkor.audioplayer.`interface`.MenuInterface
import com.wahkor.audioplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.databinding.ActivityPlayerBinding
import com.wahkor.audioplayer.model.DBPlaylist
import com.wahkor.audioplayer.service.AudioService
import com.wahkor.audioplayer.viewmodel.PlayerActivityModel
import com.wahkor.audioplayer.viewmodel.PlayerModel

class PlayerActivity : AppCompatActivity(),MenuInterface {
    private val binding:ActivityPlayerBinding by lazy { ActivityPlayerBinding.inflate(layoutInflater)}
    private lateinit var adapter:PlaylistAdapter
    private val audioPlaylist=MutableLiveData<DBPlaylist>()

    private lateinit var viewModel: PlayerModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initial()
        viewModel.playlist.observe(this,{
            adapter= PlaylistAdapter(it){ newList, action ,position->
                viewModel.recyclerCallback(newList,action,position).also {
                    if (newList.size==0){
                        startActivity(Intent(this,PlayerActivity::class.java))
                    }
                }
            }
            binding.PlayerRecycler.adapter=adapter
            val callback = CustomItemTouchHelperCallback(adapter)
            val itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(binding.PlayerRecycler)
            adapter.notifyDataSetChanged()
        })

            audioPlaylist.observe(this,{
            binding.PlayerSeekBar.max=it.song.duration.toInt()
            binding.playerPlaylistName.text=it.tableName
            binding.PlayerTitle.text=it.song.title
            binding.PlayerRecycler.scrollToPosition(it.position)
           // binding.PlayerPlay.setImageDrawable(setPlayBTNImage(it.mediaState))
        })
        setButtonListener()
        viewModel.duration.observe(this,{binding.PlayerTvDue.text=it})
        viewModel.currentPosition.observe(this,{binding.PlayerTvPass.text=it})
        viewModel.progress.observe(this,{binding.PlayerSeekBar.progress=it})
        viewModel.toast.observe(this,{
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        })
    }


    private fun setButtonListener() {
        binding.PlayerPlay.setOnClickListener {
            //audioService.playPauseBTN()
        }
        binding.PlayerPrev.setOnClickListener {
           // audioService.controlCommand("prev")
             }
        binding.PlayerNext.setOnClickListener {// audioService.controlCommand("next")
        }

        binding.PlayerSeekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    //audioService.seekTo(progress)
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

    override fun initial() {
        binding.PlayerMenu.setOnClickListener {
            setOnMenuClick(this, PopupMenu(this,binding.PlayerMenu),viewModel.tableName){
                    intent ->  startActivity(intent)
            }
        }
        binding.PlayerSetting.setOnClickListener {
            setOnSettingClick(this, PopupMenu(this,binding.PlayerSetting)){
                    intent ->  startActivity(intent)
            }
        }
        binding.PlayerRecycler.layoutManager=LinearLayoutManager(this)
        viewModel= ViewModelProvider.AndroidViewModelFactory(Application()).create(
            PlayerModel::class.java)
        viewModel.setSongInfo()
    }

}
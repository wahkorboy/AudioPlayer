package com.wahkor.audioplayer

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.audioplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.databinding.ActivityPlayerBinding
import com.wahkor.audioplayer.helper.Constants.ITEM_CLICK
import com.wahkor.audioplayer.helper.Constants.ITEM_MOVE
import com.wahkor.audioplayer.helper.Constants.ITEM_REMOVE
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.model.DBPlaylist
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.viewmodel.PlayerModel29


class PlayerActivity : AppCompatActivity() {
    private lateinit var adapter: PlaylistAdapter
    private var dbPlaylist = MutableLiveData<DBPlaylist>()
    private lateinit var viewModel: PlayerModel29
    private var scroll=false
    private val binding: ActivityPlayerBinding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel =
            ViewModelProvider.AndroidViewModelFactory(Application()).create(PlayerModel29::class.java)
        viewModel.build(this)
        binding.PlayerRecycler.layoutManager = LinearLayoutManager(this)
        setSongInfo()
        initial()
        viewModel.playerState.observe(this,{
            binding.PlayerTitle.text=it.title
            binding.PlayerSeekBar.max=it.duration
            binding.PlayerSeekBar.progress=it.current
            binding.PlayerTvPass.text=it.tvPass
            binding.PlayerTvDue.text=it.tvDue
            binding.PlayerPlay.setImageDrawable(resources.getDrawable(it.playBTN,null))
        })
        viewModel.change.observe(this,{
            setSongInfo()
            scroll=true
        })
        viewModel.toast.observe(this,{
            toast(it)
        })
        viewModel.toast.observe(this,{
            toast(it)
        })
    }

    private lateinit var playlist:ArrayList<Song>
    private fun setSongInfo() {
        dbPlaylist.value = DBConnect().getDBPlaylist(this)
        playlist= dbPlaylist.value!!.playlist
        adapter = PlaylistAdapter(playlist) { newList, action, _ ->
            when(action){
                ITEM_CLICK->{
                    DBConnect().updatePlaylist(this, newList, dbPlaylist.value!!.tableName)
                    viewModel.playlistAction()
                    scroll=false
                }
                ITEM_MOVE->{
                    DBConnect().updatePlaylist(this, newList, dbPlaylist.value!!.tableName)

                }
                ITEM_REMOVE->{
                    DBConnect().updatePlaylist(this, newList, dbPlaylist.value!!.tableName)
                    setSongInfo()

                }
            }
        }
        binding.PlayerRecycler.layoutManager=LinearLayoutManager(this)
        binding.PlayerRecycler.adapter = adapter
        adapter.notifyDataSetChanged()
        val callback = CustomItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.PlayerRecycler)
        if(scroll){
            binding.PlayerRecycler.smoothScrollToPosition(dbPlaylist.value!!.position)
            scroll=false

        }
    }

    private fun initial() {
        binding.PlayerPlay.setOnClickListener {
            viewModel.actionClick()}
        binding.PlayerPrev.setOnClickListener { viewModel.prevClick();setSongInfo()}
        binding.PlayerNext.setOnClickListener { viewModel.nextClick();setSongInfo()}
        binding.PlayerSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    viewModel.seekbar(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }


    private fun toast(message: Any) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {

    }

}
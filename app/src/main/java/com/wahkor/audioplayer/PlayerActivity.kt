package com.wahkor.audioplayer

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.media.MediaMetadata.METADATA_KEY_DISPLAY_TITLE
import android.media.browse.MediaBrowser
import android.media.session.PlaybackState
import android.media.session.PlaybackState.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.databinding.ActivityFeatureTestBinding
import com.wahkor.audioplayer.databinding.ActivityPlayerBinding
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.model.DBPlaylist
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import com.wahkor.audioplayer.viewmodel.PlayerModel
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.random.Random


class PlayerActivity : AppCompatActivity() {
    private lateinit var adapter: PlaylistAdapter
    private var dbPlaylist = MutableLiveData<DBPlaylist>()
    private lateinit var viewModel: PlayerModel
    private val binding: ActivityPlayerBinding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel =
            ViewModelProvider.AndroidViewModelFactory(Application()).create(PlayerModel::class.java)
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
        })
    }

    private lateinit var playlist:ArrayList<Song>
    private fun setSongInfo() {
        dbPlaylist.value = DBConnect().getDBPlaylist(this)
        playlist= dbPlaylist.value!!.playlist
        adapter = PlaylistAdapter(playlist) { newList, action, position ->
            DBConnect().updatePlaylist(this, newList, dbPlaylist.value!!.tableName)
            viewModel.playlistAction(this, newList, action, position)
            setSongInfo()
        }
        binding.PlayerRecycler.layoutManager=LinearLayoutManager(this)
        binding.PlayerRecycler.adapter = adapter
        adapter.notifyDataSetChanged()
        binding.PlayerRecycler.scrollToPosition(dbPlaylist.value!!.position)
    }

    private fun initial() {
        binding.PlayerPlay.setOnClickListener {
            viewModel.actionClick()}
        binding.PlayerPrev.setOnClickListener { viewModel.prevClick()}
        binding.PlayerNext.setOnClickListener { viewModel.nextClick()}
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


}
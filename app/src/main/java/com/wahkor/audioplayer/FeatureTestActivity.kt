package com.wahkor.audioplayer

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.media.MediaMetadata.METADATA_KEY_DISPLAY_TITLE
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.databinding.ActivityFeatureTestBinding
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.model.DBPlaylist
import com.wahkor.audioplayer.service.AudioService
import com.wahkor.audioplayer.viewmodel.FeatureModel
import com.wahkor.audioplayer.viewmodel.PlayerModel
import kotlinx.coroutines.*
import kotlin.random.Random


class FeatureTestActivity : AppCompatActivity() {
    private val dbConnect = DBConnect()
    private lateinit var adapter: PlaylistAdapter
    private val dbPlaylist = MutableLiveData<DBPlaylist>()
    private lateinit var viewModel: FeatureModel
    private val binding: ActivityFeatureTestBinding by lazy {
        ActivityFeatureTestBinding.inflate(layoutInflater)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel =
            ViewModelProvider.AndroidViewModelFactory(Application()).create(FeatureModel::class.java)
        viewModel.build(this)
        binding.fRecycler.layoutManager = LinearLayoutManager(this)
        setSongInfo()
        dbPlaylist.observe(this,{
            binding.testShowtext.textSize=15f
            binding.testShowtext.text=it.song.title
            binding.actionBTN.setImageDrawable(resources.getDrawable(viewModel.playBTN,null))
        })

    }

    private fun setSongInfo() {
        dbPlaylist.value = dbConnect.getDBPlaylist(this)
        val playlist = dbPlaylist.value!!.playlist
        adapter = PlaylistAdapter(playlist) { newList, action, position ->
            dbConnect.updatePlaylist(this, newList, dbPlaylist.value!!.tableName)
            viewModel.playlistAction()
            setSongInfo()
        }
        binding.fRecycler.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun toast(message: Any) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }

    fun prevBTN(view: View) {
        viewModel.prevClick()

    }

    fun nextBTN(view: View) {
        viewModel.nextClick()
    }

    fun actionBTN(view: View) {
        viewModel.actionClick()
    }

}
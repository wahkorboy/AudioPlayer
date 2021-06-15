package com.wahkor.audioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.audioplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.model.Song

class PlayerActivity : AppCompatActivity() {
    private lateinit var menu:ImageButton
    private lateinit var setting:ImageButton
    private lateinit var playlistName:TextView
    private lateinit var playerTitle:TextView
    private lateinit var tvPass:TextView
    private lateinit var tvDue:TextView
    private lateinit var seekBar: SeekBar
    private lateinit var prevBTN:ImageButton
    private lateinit var playBTN:ImageButton
    private lateinit var nextBTN:ImageButton
    private lateinit var recyclerView: RecyclerView

    private lateinit var songs:ArrayList<Song>
    private lateinit var playlistManager: PlaylistManager
    private lateinit var adapter:PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setView()
        playlistManager=PlaylistManager(this)
        songs=playlistManager.playlist
        adapter= PlaylistAdapter(songs){ newList, action ->
            playlistManager.updatePlaylist(newList){result ->
                songs=result
                adapter.notifyDataSetChanged()
            }
        }
        playlistName.text=playlistManager.tableName
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter=adapter
        val callback = CustomItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        adapter.notifyDataSetChanged()
    }

    private fun setView() {
        menu=findViewById(R.id.PlayerMenu)
        setting=findViewById(R.id.PlayerSetting)
        playlistName=findViewById(R.id.playerPlaylistName)
        playerTitle=findViewById(R.id.PlayerTitle)
        tvPass=findViewById(R.id.PlayerTvPass)
        tvDue=findViewById(R.id.PlayerTvDue)
        seekBar=findViewById(R.id.PlayerSeekBar)
        prevBTN=findViewById(R.id.PlayerPrev)
        playBTN=findViewById(R.id.PlayerPlay)
        nextBTN=findViewById(R.id.PlayerNext)
        recyclerView=findViewById(R.id.PlayerRecycler)
    }
}
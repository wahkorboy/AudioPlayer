package com.wahkor.audioplayer

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.audioplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import com.wahkor.audioplayer.service.PlayListView
import com.wahkor.audioplayer.service.STATE_PAUSE
import com.wahkor.audioplayer.service.STATE_PLAYING

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
    private lateinit var adapter:PlaylistAdapter

    private val audioService=AudioService()
    private val handler=Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var playlist:PlayListView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playlist=audioService.getPlaylistView
        songs=playlist!!.playlist





        setView()
        setButtonListener()
        adapter= PlaylistAdapter(songs){ newList, action ->
            audioService.updatePlaylist(newList){result ->
                songs=result
                adapter.notifyDataSetChanged()
                audioService.controlCommand("current"){playlistView->
                    playlist=playlistView
                    recyclerView.scrollToPosition(playlist!!.position)
                }
            }
        }
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter=adapter
        val callback = CustomItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        adapter.notifyDataSetChanged()

        setRunnable()
    }

    private fun setButtonListener() {
        playBTN.setOnClickListener { val mediaState=audioService.playPauseBTN()
            playlist=audioService.getPlaylistView
            postGetPlaylist()
            playBTN.setImageDrawable(
                setPlayBTNImage(mediaState)
            )
        }
        prevBTN.setOnClickListener { audioService.controlCommand("prev"){playlistView->
            playlist=playlistView
            postGetPlaylist()
        }  }
        nextBTN.setOnClickListener { audioService.controlCommand("next"){playlistView->
            playlist=playlistView
            postGetPlaylist()
        }  }
    }
    private fun postGetPlaylist(){
        songs= playlist!!.playlist
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(playlist!!.position)
        seekBar.max= playlist!!.song?.duration?.toInt() ?: 0

    }
    private fun setRunnable() {
        runnable= Runnable {
            val playlist=audioService.getPlaylistView
            playlist.song?.let {
                playlistName.text=playlist.tableName!!
                playerTitle.text=it.title
                seekBar.progress=playlist.currentPosition

            }
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)

    }
private fun setPlayBTNImage(mediaState:Int):Drawable?{

    return if (mediaState== STATE_PLAYING)
        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_pause_24, null)
    else
        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_play_arrow_24, null)
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

        playlist?.song?.let {
            playlistName.text=playlist!!.tableName!!
            playerTitle.text=it.title
        }
        seekBar.setOnSeekBarChangeListener(@SuppressLint("AppCompatCustomView")
        object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    audioService.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                TODO("Not yet implemented")
            }

        })

    }

}
package com.wahkor.audioplayer

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
            val newPlaylist=audioService.getPlaylistView
            newPlaylist.song?.let {
                if (it.duration.toInt() != seekBar.max){
                    songs=newPlaylist.playlist
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(newPlaylist.position)
                }
                playlistName.text=newPlaylist.tableName!!
                playerTitle.text=it.title
                seekBar.progress=newPlaylist.currentPosition
                setTv(newPlaylist.currentPosition, newPlaylist.song!!.duration.toInt())

            }
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)

    }

    private fun setTv(current:Int,duration:Int) {
        tvPass.text=millSecToString(current)
        tvDue.text=millSecToString(duration-current)
    }
    private fun millSecToString(millSecs:Int):String{
        var secs=millSecs/1000
        var minute=secs/60
        val hours=minute/60
        minute -= hours * 60
        secs=secs-minute*60-hours*60*60
        var text=if (hours==0)"" else "$hours:"
        text+=if(minute<10)"0$minute:" else "$minute:"
        text+=if(secs<10)"0$secs" else "$secs"
        return text
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
        seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
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


}
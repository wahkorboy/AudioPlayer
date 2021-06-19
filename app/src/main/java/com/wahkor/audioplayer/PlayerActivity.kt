package com.wahkor.audioplayer

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.audioplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import com.wahkor.audioplayer.service.STATE_PAUSE
import com.wahkor.audioplayer.service.STATE_PLAYING
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {

    private lateinit var songs:ArrayList<Song>
    private lateinit var song: Song
    private lateinit var adapter:PlaylistAdapter

    private val audioService=AudioService()
    private val handler=Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        songs=audioService.getPlayerInfo.value!!.playlist
        adapter= PlaylistAdapter(songs){ newList, action ->
            audioService.updatePlaylist(newList){result ->
                songs=result
                adapter.notifyDataSetChanged()
                audioService.controlCommand("current")
            }
        }
        PlayerRecycler.layoutManager=LinearLayoutManager(this)
        PlayerRecycler.adapter=adapter
        val callback = CustomItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(PlayerRecycler)
        adapter.notifyDataSetChanged()

        setRunnable()
        setButtonListener()
        audioService.getPlayerInfo.observe(this,{
            PlayerSeekBar.max=it.song.duration.toInt()
            playerPlaylistName.text=it.tableName
            PlayerTitle.text=it.song.title
            songs=it.playlist
            song=it.song
            adapter.notifyDataSetChanged()
            PlayerRecycler.scrollToPosition(it.position)
            setPlayBTNImage(it.mediaState)
        })
    }

    private fun setButtonListener() {
        PlayerPlay.setOnClickListener {
            audioService.playPauseBTN()
        }
        PlayerPrev.setOnClickListener {
            audioService.controlCommand("prev") }
        PlayerNext.setOnClickListener { audioService.controlCommand("next") }

        PlayerSeekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
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
    private fun setRunnable() {
        runnable= Runnable {
            val current=audioService.getCurrentPosition
            PlayerSeekBar.progress=audioService.getCurrentPosition
            setTV(current,song.duration.toInt())
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)

    }

    private fun setTV(current: Int, duration: Int) {
        PlayerTvDue.text=millSecToString(duration-current)
        PlayerTvPass.text=millSecToString(current)

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
Toast.makeText(this,"sss",Toast.LENGTH_SHORT).show()
    return if (mediaState== STATE_PAUSE)
        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_pause_24, null)
    else
        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_play_arrow_24, null)
}
    }
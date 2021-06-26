package com.wahkor.audioplayer.viewmodel

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.helper.Constants.ITEM_CLICK
import com.wahkor.audioplayer.helper.Constants.actionNext
import com.wahkor.audioplayer.helper.Constants.actionPrevious
import com.wahkor.audioplayer.helper.DBConnect
import com.wahkor.audioplayer.model.DBPlaylist
import com.wahkor.audioplayer.model.PlayerState
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.MusicService
import java.lang.Runnable
import java.util.ArrayList
import kotlin.random.Random

class PlayerModel : ViewModel() {

    // set observer and UI
    val dbPlaylist=MutableLiveData<DBPlaylist>()
    val toast=MutableLiveData<String>()

    inner class BTNListener(val context: Context, private val playerPlay: ImageButton, val action:String) :
        View.OnClickListener {
        override fun onClick(v: View?) {
            val intent=Intent(context,MusicService::class.java)
            intent.action=action
            context.startService(intent)
        }
    }
private val playerState=MutableLiveData<PlayerState>()
    //
    var runID = 0
    //var duration=0
// remote command


    private lateinit var intent: Intent
    fun build(context: Context) {
        intent= Intent(context,MusicService::class.java)
        context.startService(intent)

        //remote.play()
    }

fun updatePlayBTN(context: Context,isRunning:Boolean,playerBTN:ImageButton){
    if (isRunning)playerBTN.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_pause_24))
    else playerBTN.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_play_arrow_24))
}

    fun millSecToString(millSecs: Int): String {
        var secs = millSecs / 1000
        var minute = secs / 60
        val hours = minute / 60
        minute -= hours * 60
        secs = secs - minute * 60 - hours * 60 * 60
        var text = if (hours == 0) "" else "$hours:"
        text += if (minute < 10) "0$minute:" else "$minute:"
        text += if (secs < 10) "0$secs" else "$secs"
        return text
    }

    fun updateUI(context: Context, updateui: Boolean):Boolean {
        if (updateui){
            dbPlaylist.value=DBConnect().getDBPlaylist(context)
        }
            return false
    }

    fun playListAction(context: Context, newList: ArrayList<Song>, action: String, position: Int) {
        when(action){
            ITEM_CLICK->{
                val result= dbPlaylist.value?.let {
                    DBConnect().updatePlaylist(context,newList,
                        it.tableName)
                }?:false
                if (result){
                    dbPlaylist.value=DBConnect().getDBPlaylist(context)
                }
            }
        }

    }


}
package com.wahkor.audioplayer

import android.content.Context
import android.widget.Toast
import com.wahkor.audioplayer.Constants.COMMAND_NEXT
import com.wahkor.audioplayer.Constants.COMMAND_PLAY
import com.wahkor.audioplayer.Constants.COMMAND_PREV
import com.wahkor.audioplayer.database.PlayListDB
import com.wahkor.audioplayer.database.PlaylistStatusDb
import com.wahkor.audioplayer.model.Song

class PlaylistManager() {
    private lateinit var  db :PlayListDB
    private lateinit var statusDb :PlaylistStatusDb
    private var playlist=ArrayList<Song>()
    private var tableName:String?=null
    val getPlaylist: ArrayList<Song> get() = playlist
    val getTableName: String? get() = tableName
    fun build(context: Context){
        statusDb= PlaylistStatusDb(context)
        db=PlayListDB(context)
        tableName=statusDb.getTableName
        playlist=db.getData(tableName!!)
    }
    fun getSong(query: String,callback:(song:Song?,position:Int)->Unit) {
        var position = 0
        var prevPosition=0
        var nextPosition=0
        if (getPlaylist.size != 0) {
            for (i in 0 until getPlaylist.size){

                if (getPlaylist[i].isPlaying) {
                    position = i
                    getPlaylist[i].isPlaying=false
                    prevPosition=if (position==0)getPlaylist.size-1 else position-1
                    nextPosition=if (position == getPlaylist.size-1) 0 else position+1
                }
            }
        } else {
            callback(null,0)
        }
        when (query) {
            COMMAND_PLAY -> {
                updateIsPlaying(position)
                callback(playlist[position],position)
            }
            COMMAND_NEXT -> {
                updateIsPlaying(nextPosition)
                callback(playlist[nextPosition],nextPosition)
            }
            COMMAND_PREV -> {
                updateIsPlaying(prevPosition)
                callback(playlist[prevPosition],prevPosition)
            }
            else -> callback(null,0)
        }

    }

    private fun updateIsPlaying(position: Int) {
        getPlaylist[position].isPlaying=true
        db.setData(getTableName!!,playlist)
    }

    fun updatePlaylist(
        list: ArrayList<Song>,
        name: String = getTableName!!,
        callback: (ArrayList<Song>) -> Unit
    ) {
        if (db.getName.contains(name)) {
            if (list.size != getPlaylist.size && name == "playlist_default") {
                callback(getPlaylist)
            } else {
                db.setData(name, list)
                callback(list)
            }

        }
    }
}
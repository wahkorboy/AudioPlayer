package com.wahkor.audioplayer.helper

import android.content.Context
import com.wahkor.audioplayer.database.PlayListDB
import com.wahkor.audioplayer.database.PlaylistStatusDb
import com.wahkor.audioplayer.helper.Constants.COMMAND_NEXT
import com.wahkor.audioplayer.helper.Constants.COMMAND_PLAY
import com.wahkor.audioplayer.helper.Constants.COMMAND_PREV
import com.wahkor.audioplayer.helper.Constants.DEFAULT_PLAYLIST
import com.wahkor.audioplayer.model.DBPlaylist
import com.wahkor.audioplayer.model.Song


class DBConnect {
    private lateinit var db: PlayListDB
    private lateinit var statusDb: PlaylistStatusDb
    private var playlist = ArrayList<Song>()
    private lateinit var tableName: String
    private lateinit var song:Song
    private var position=0
    private var connected = false
    private lateinit var allTable:ArrayList<String>
    private fun connecting(context: Context) {
        if (connected) return
        statusDb = PlaylistStatusDb(context)
        db = PlayListDB(context)
        tableName = statusDb.getTableName!!
        playlist = db.getData(tableName)
        allTable= db.getName
        getCurrentSong(playlist){currentSong:Song,currentPosition:Int ->
            song=currentSong
            position=currentPosition
        }
        connected = true
    }

    private fun getCurrentSong(
        managePlaylist:ArrayList<Song>,
        callback: (Song, Int) -> Unit)
    :Song {   //search for song with isPlaying is true
        var currentPosition=0
        for (i in 0 until managePlaylist.size){
            if(managePlaylist[i].isPlaying)currentPosition=i
        }
        callback(managePlaylist[currentPosition],currentPosition)
        return managePlaylist[currentPosition]
    }

    fun getDBPlaylist(context: Context): DBPlaylist{   // get  Playlist Information
        connecting(context)
        return DBPlaylist(playlist,song,tableName,position,allTable)
    }
    fun updatePlaylist(
        context: Context,
        managePlaylist:ArrayList<Song>,
        manageTable:String):Boolean{
        connecting(context)
        if (manageTable== DEFAULT_PLAYLIST &&   // prevent remove Song from Default playlist
            managePlaylist.size != playlist.size)
            {
            return false
        }
        if (managePlaylist.size>0){
            db.setData(manageTable,managePlaylist)
            connected=false
            connecting(context)

        }else{  //delete Empty Playlist
            db.deleteTable(manageTable)
            if (manageTable==tableName){
                statusDb.setTableName(DEFAULT_PLAYLIST)
                connected=false
                connecting(context)
            }
        }
        return true
    }

    private fun controlCommand(
        context: Context,
        query: String
    ):DBPlaylist {
        connecting(context)
        when (query) {
            COMMAND_NEXT -> {
                getCurrentSong(playlist) { _, managePosition ->
                    var newPosition = 0
                    if (managePosition < playlist.size - 1) {
                        newPosition=managePosition+1
                    }
                    val managePlaylist=playlist
                    managePlaylist[managePosition].isPlaying=false
                    managePlaylist[newPosition].isPlaying=true
                    updatePlaylist(context,managePlaylist,tableName)
                }
            }
            COMMAND_PREV -> {
                getCurrentSong(playlist) { _, managePosition ->
                    var newPosition = playlist.size-1
                    if (managePosition > 0) {
                        newPosition = managePosition - 1
                    }
                    val managePlaylist = playlist
                    managePlaylist[managePosition].isPlaying=false
                    managePlaylist[newPosition].isPlaying = true
                    updatePlaylist(context, managePlaylist, tableName)
                }
            }
        }
        return getDBPlaylist(context)

    }
}
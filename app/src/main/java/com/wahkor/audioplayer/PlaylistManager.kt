package com.wahkor.audioplayer

import android.content.Context
import com.wahkor.audioplayer.database.PlayListDB
import com.wahkor.audioplayer.database.PlaylistStatusDb
import com.wahkor.audioplayer.model.Song

class PlaylistManager(context: Context) {
    private val db = PlayListDB(context)
    private val statusDb = PlaylistStatusDb(context)
    val playlist: ArrayList<Song> get() = db.getData(statusDb.getTableName!!)
    val tableName: String get() = statusDb.getTableName!!
    fun getSong(query: String): Song? {
        var position = 0
        var prevPositon=0
        var nextPosition=0
        if (playlist.size != 0) {
            for (i in 0 until playlist.size){

                if (playlist[i].isPlaying) {
                    position = i
                }
            }
        } else {
            return null
        }
        return when (query) {
            "current" -> playlist[position]
            "next" -> if (position == playlist.size - 1) playlist[0] else playlist[position + 1]
            "prev" -> if (position == 0) playlist[playlist.size - 1] else playlist[position - 1]
            else -> null
        }

    }

    fun updatePlaylist(
        list: ArrayList<Song>,
        name: String = tableName,
        callback: (ArrayList<Song>) -> Unit
    ) {
        if (db.getName.contains(name)) {
            if (list.size != playlist.size && name == "playlist_default") {
                callback(playlist)
            } else {
                db.setData(name, list)
                callback(list)
            }

        }
    }
}
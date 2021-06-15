package com.wahkor.audioplayer

import android.content.Context
import com.wahkor.audioplayer.database.PlayListDB
import com.wahkor.audioplayer.database.PlaylistStatusDb
import com.wahkor.audioplayer.model.Song

class PlaylistManager(context: Context) {
    private val db=PlayListDB(context)
    private val statusDb=PlaylistStatusDb(context)
    val playlist:ArrayList<Song> get() = db.getData(statusDb.getTableName!!)
    val tableName:String get() = statusDb.getTableName!!
}
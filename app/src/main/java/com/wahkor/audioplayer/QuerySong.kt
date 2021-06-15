package com.wahkor.audioplayer

import android.content.Context
import android.provider.MediaStore
import com.wahkor.audioplayer.database.PlayListDB
import com.wahkor.audioplayer.database.PlaylistStatusDb
import com.wahkor.audioplayer.model.Song
import kotlin.collections.ArrayList
import kotlin.random.Random

class QuerySong(private val context: Context) {
    private var songs=ArrayList<Song>()
    private val db= PlayListDB(context)
    private val statusDb= PlaylistStatusDb(context)
    fun build(callback: () -> Unit){
        loadMusic()
        callback()
    }

    private fun loadMusic() {
        val columns = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TITLE,
        )

        val allMusic = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection=MediaStore.Audio.Media.IS_MUSIC
        val cursor=context.contentResolver.query(allMusic,null,selection,null,null)
        if(cursor != null){
            while (cursor.moveToNext()){
                var item=0
                songs.add(
                    Song(
                        cursor.getString(cursor.getColumnIndex(columns[item++])),
                        cursor.getString(cursor.getColumnIndex(columns[item++])),
                        cursor.getString(cursor.getColumnIndex(columns[item++])),
                        cursor.getLong(cursor.getColumnIndex(columns[item++])),
                        false,
                        cursor.getString(cursor.getColumnIndex(columns[item])),
                    )
                )
            }

            cursor.close()
            val currentSong= Random.nextInt(0, songs.size-1)
            songs[currentSong].isPlaying=true
            songs.sortBy { it.folderPath }
            var tableName="playlist_default"
            val oldData=db.getData(tableName)
            if (oldData.size !=songs.size){
                db.setData(tableName,songs)
            }
            val tableStatus=statusDb.getTableName
            tableStatus?.let {  }?: kotlin.run { statusDb.setTableName(tableName) }
        }

    }
}
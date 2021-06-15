package com.wahkor.audioplayer

import android.content.Context
import android.provider.MediaStore

data class Song(
    val title:String
        )
class QuerySong(val context: Context) {
    private var songs=ArrayList<Song>()
    fun build(){

    }

    private fun loadMusic() {
        val columns = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TITLE,
        )

        var songList=ArrayList<Song>()
        val allMusic = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection=MediaStore.Audio.Media.IS_MUSIC
        val cursor=context.contentResolver.query(allMusic,null,selection,null,null)
        if(cursor != null){
            while (cursor.moveToNext()){
                var item=0
                songList.add(
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
            cursor?.close()
            val currentSong= Random.nextInt(0, songList.size-1)
            songList[currentSong].is_playing=true
            songList.sortBy { it.folderPath }
            db.setData(tableName,songList)
            sleepTimeSetup()
        }

    }
}
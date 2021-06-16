package com.wahkor.audioplayer.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.wahkor.audioplayer.model.Song
import java.lang.Exception

class PlayListDB(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VER) {
    companion object{
        const val DATABASE_NAME="MediaPlayer.db"
        const val DATABASE_VER=1

        private const val col_ALBUM = "album"
        private const val col_ARTIST = "artist"
        private const val col_DATA = "data"
        private const val col_DURATION = "duration"
        private const val col_isPlaying = "is_playing"
        private const val col_TITLE = "title"

        const val dataSet="$col_ALBUM TEXT,$col_ARTIST TEXT,$col_DATA TEXT,$col_DURATION INTEGER,$col_isPlaying INTEGER,$col_TITLE TEXT"
    }

    override fun onCreate(db: SQLiteDatabase?) {
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
    fun getData(tableName:String):ArrayList<Song> {
            val list=ArrayList<Song>()
            val db=this.writableDatabase
        try {   // create table if not exists
            db.execSQL("create table $tableName($dataSet)")
        }catch (e:Exception){}

            val cursor=db.rawQuery("select * from $tableName",null)
            if (cursor != null){
                while (cursor.moveToNext()){
                    list.add(
                        Song(
                            cursor.getString(cursor.getColumnIndex(col_ALBUM)),
                            cursor.getString(cursor.getColumnIndex(col_ARTIST)),
                            cursor.getString(cursor.getColumnIndex(col_DATA)),
                            cursor.getLong(cursor.getColumnIndex(col_DURATION)),
                            cursor.getInt(cursor.getColumnIndex(col_isPlaying))==1,
                            cursor.getString(cursor.getColumnIndex(col_TITLE))
                        )
                    )
                }
            }
            cursor.close()
            db.close()
            return list
        }
    fun setData(tableName: String,list:ArrayList<Song>){
        val db=this.writableDatabase
        try {   // create table if not exists
            db.execSQL("create table $tableName($dataSet)")
        }catch (e:Exception){}
        db.delete(tableName,null,null) // delete all data in table
        if (list.size>0){
            for(song in list){
                val values=ContentValues()
                values.put(col_ALBUM,song.album)
                values.put(col_ARTIST,song.artist)
                values.put(col_DATA,song.data)
                values.put(col_DURATION,song.duration)
                values.put(col_isPlaying,if (song.isPlaying) 1 else 0)
                values.put(col_TITLE,song.title)
                db.insert(tableName,null,values)
            }
        }
        db.close()
    }

    fun createTable(tableName: String, list: ArrayList<Song>) {
        val db=this.writableDatabase
        try {
            db.execSQL("create table $tableName ($dataSet)")
        }catch (e:Exception){}
        db.close()
        setData(tableName,list)

    }

    fun deleteTable(tableName: String):ArrayList<String> {
        val db=this.writableDatabase
        try {
            db.execSQL("drop table if exists $tableName")
            db.close()
        }catch (e:Exception){}
        return getName
    }

    val getName:ArrayList<String>
    get() {
        val list:ArrayList<String> =ArrayList()
        val db=this.writableDatabase
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        if (cursor != null){
            while (cursor.moveToNext()){
                list.add(
                    cursor.getString(cursor.getColumnIndex("name"))
                )
            }
        }
        cursor.close()
        db.close()

        return list
    }
}
package com.wahkor.audioplayer.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception

class PlaylistStatusDb(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER) {
    companion object {
        const val DATABASE_NAME = "MediaPlayer.db"
        const val DATABASE_VER = 1
        const val tableName = "statusTable"
        private const val col_id = "name"
        const val dataSet = "$col_id Text"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("create table $tableName ( $dataSet ) ")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists $tableName")
        onCreate(db)
    }

    val getTableName: String?
        get() {
            val db = this.writableDatabase
            // create table if not exists
            db.execSQL("create table if not exists $tableName(${dataSet})")

            val cursor = db.rawQuery("select * from $tableName", null)
            var text: String? = null
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    text = cursor.getString(cursor.getColumnIndex(col_id))
                }
            }
            cursor?.close()
            db.close()
            return text
        }

    fun setTableName(saveTable: String) {
        val db = this.writableDatabase
        // create table if not exists
        db.execSQL("create table if not exists $tableName(${PlayListDB.dataSet})")

        val values = ContentValues()
        values.put(col_id, saveTable)
        db.delete(tableName, null, null)
        db.insert(tableName, null, values)
        db.close()

    }
}


package com.wahkor.audioplayer.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.database.PlayListDB
import com.wahkor.audioplayer.database.PlaylistStatusDb
import com.wahkor.audioplayer.service.AudioService

class PlaylistManagerModel:ViewModel() {
    private lateinit var db:PlayListDB
    private lateinit var statusDb:PlaylistStatusDb
    @SuppressLint("StaticFieldLeak")
    private val audioService=AudioService()
    val tableList=MutableLiveData<ArrayList<String>>()
    val selectedTable=MutableLiveData<String>()
    fun build(context: Context,tableName:String) {
        db= PlayListDB(context)
        statusDb= PlaylistStatusDb(context)
        selectedTable.value=statusDb.getTableName!!

        setTableList(tableName)
    }
    private fun setTableList(tableName: String){
        val list=db.getName
        val newList=ArrayList<String>()
        for (item in list){
            if(item.contains("playlist_")){
                newList.add(item)
            }
        }
        newList.add(tableName)
        tableList.value=newList

    }

    fun delete(tableName: String){
        val status=statusDb.getTableName
        if (tableName==status){
            statusDb.setTableName("playlist_default")
            audioService.changePlaylist("playlist_default")
        }
        db.deleteTable(tableName)
        setTableList(statusDb.getTableName!!)

    }

    fun saveSubmit(saveName: String, tableName: String):Boolean {
        return if(saveName != "playlist_default" && saveName !="default" && (saveName.length in 3..35)) {
            val data = db.getData(tableName)
            val dbName="playlist_$saveName"
            db.setData(dbName, data)
            openSubmit(dbName)
            true
        }else false
    }

    fun openSubmit(openName: String) {
        val status=statusDb.getTableName
        if(openName != status){
            audioService.changePlaylist(openName)
            statusDb.setTableName(openName)
        }

    }

}
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
    fun build(context: Context,tableName:String) {
        db= PlayListDB(context)
        statusDb= PlaylistStatusDb(context)
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

    }

    fun saveSubmit(saveName: String, tableName: String):Boolean {
        return if(saveName != "playlist_default"){
            val data=db.getData(tableName)
            db.setData(saveName,data)
            true
        }else false
    }

}
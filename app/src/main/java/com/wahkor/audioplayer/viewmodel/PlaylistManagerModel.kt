package com.wahkor.audioplayer.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.database.PlayListDB

class PlaylistManagerModel:ViewModel() {
    private lateinit var db:PlayListDB
    val tableList=MutableLiveData<ArrayList<String>>()
    fun build(context: Context) {
        db= PlayListDB(context)
        val list=db.getName
        val newList=ArrayList<String>()
        for (item in list){
            if(item.contains("playlist_")){
                newList.add(item)
            }
        }
        tableList.value=newList

    }
}
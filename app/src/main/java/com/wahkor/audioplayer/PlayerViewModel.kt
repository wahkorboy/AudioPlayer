package com.wahkor.audioplayer

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.database.PlayListDB
import com.wahkor.audioplayer.database.PlaylistStatusDb
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import kotlinx.coroutines.*

class PlayerViewModel:ViewModel() {
    var playlist=MutableLiveData<ArrayList<Song>>()
    private val modelJob= SupervisorJob()
    private val mainScope= CoroutineScope(Dispatchers.Main+modelJob)
    /*fun retrievePlaylist(context: Context){
        mainScope.launch(Dispatchers.IO){
            val db=PlayListDB(context)
            val statusDb=PlaylistStatusDb(context)
            while (true){
                delay(3000)
                val tableName=statusDb.getTableName
                val pls=db.getData(tableName!!)
                withContext(Dispatchers.Main){
                    if(playlist.value!! != pls)
                    playlist.value=pls
                }
            }
        }

    }*/
}
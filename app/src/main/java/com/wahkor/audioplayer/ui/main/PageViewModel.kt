package com.wahkor.audioplayer.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.model.SelectedSong
import com.wahkor.audioplayer.model.Song

class PageViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    val playlist=MutableLiveData<ArrayList<SelectedSong>>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }
    fun setPlaylist(mPlaylist:ArrayList<Song>){
        val selectedList=ArrayList<SelectedSong>()
        for (item in mPlaylist){
            selectedList.add(
                SelectedSong(false,item)
            )
        }
        playlist.value=selectedList
    }
    fun updatePlaylist(selectedList:ArrayList<SelectedSong>){
        playlist.value=selectedList
    }

}
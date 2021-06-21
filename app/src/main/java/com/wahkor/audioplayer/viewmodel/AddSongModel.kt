package com.wahkor.audioplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.model.SelectedSong
import com.wahkor.audioplayer.model.Song
import java.util.ArrayList

class AddSongModel:ViewModel() {
    val list= MutableLiveData<ArrayList<SelectedSong>>()
    private var rawPlaylist=ArrayList<Song>()
    fun build(playlist: ArrayList<Song>) {
        rawPlaylist=playlist
        val sPlaylist=ArrayList<SelectedSong>()
        for (song in playlist)sPlaylist.add(SelectedSong(false,song))
        list.value=sPlaylist
    }

    fun insertBy(position: Int) {
        when(position){
            1->{ rawPlaylist.sortBy { it.folderPath }}
            2->{rawPlaylist.sortBy { it.album } }
            else ->{ rawPlaylist.sortBy { it.artist } }
        }
    build(rawPlaylist)
    }

    fun updateList(position: Int) {
        val update=list.value!!
        update[position].isSelected=!update[position].isSelected
        list.value=update
    }

}
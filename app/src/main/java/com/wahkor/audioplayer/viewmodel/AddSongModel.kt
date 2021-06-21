package com.wahkor.audioplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.model.SelectedSong
import com.wahkor.audioplayer.model.Song
import java.util.ArrayList
data class SelectedList(
    var isSelected:Boolean=false,
    val name:String,
)
class AddSongModel:ViewModel() {
    val list= MutableLiveData<ArrayList<SelectedList>>()
    private var rawPlaylist=ArrayList<Song>()
    private var sortByArtist=ArrayList<SelectedList>()
    private var sortByLocation=ArrayList<SelectedList>()
    private var sortByAlbum=ArrayList<SelectedList>()
    fun build(playlist: ArrayList<Song>,sort:String) {
        rawPlaylist=playlist
        createList(sort)
    }

    private fun createList(sort: String) {
        TODO("Not yet implemented")
    }

    fun sortSetup() {
        rawPlaylist.forEach { song: Song ->
            if(!sortByArtist.contains(song.artist)) sortByArtist.add(SelectedList(false,song.artist))
            if(!sortByLocation.contains(song.artist)) sortByLocation.add(song.folderPath)
            if(!sortByAlbum.contains(song.artist)) sortByAlbum.add(song.album)
        }
    }

    fun updateList(position: Int) {
        val update=list.value!!
        update[position].isSelected=!update[position].isSelected
        //list.value=update
    }

}
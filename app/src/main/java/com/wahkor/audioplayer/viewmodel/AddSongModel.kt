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
    private val sortMode=0
    private val songMode=1
    private val byArtist="Artist"
    private val byLocation="Location"
    private val byAlbum="Album"
    val list= MutableLiveData<ArrayList<SelectedList>>()
    private var rawPlaylist=ArrayList<Song>()
    private var sortByArtist=ArrayList<String>()
    private var sortByLocation=ArrayList<String>()
    private var sortByAlbum=ArrayList<String>()
    private var selectMode=sortMode
    private var sortBy=byArtist
    fun build(playlist: ArrayList<Song>,sort:String="Artist") {
        rawPlaylist=playlist
        sortSetup()
        createList(sort)
    }

    fun createList(sort: String,index:Int=-1) {
        val preList=ArrayList<SelectedList>()
        when(index){
            -1->{ when(sort){
                    byArtist ->{sortByArtist.forEach { item->preList.add(SelectedList(false,item)) }}
                    byLocation ->{sortByLocation.forEach { item->preList.add(SelectedList(false,item)) }}
                    byAlbum ->{sortByLocation.forEach { item->preList.add(SelectedList(false,item)) }}
                    }
            }

            else->{
                when(sort){
                    byArtist ->{
                        val artist=sortByArtist[index]
                        rawPlaylist.forEach { song ->
                            if (song.artist==artist)preList.add(SelectedList(false,song.title))
                        }
                    }
                    byLocation ->{
                        val path=sortByLocation[index]
                        rawPlaylist.forEach { song ->
                            if (song.folderPath==path)preList.add(SelectedList(false,song.title))
                        }
                    }
                    byAlbum ->{
                        val album=sortByAlbum[index]
                        rawPlaylist.forEach { song ->
                            if (song.album==album)preList.add(SelectedList(false,song.title))
                        }
                    }
                }
            }
        }
        list.value=preList

    }

    private fun sortSetup() {
        rawPlaylist.forEach { song: Song ->
            if(!sortByArtist.contains(song.artist)) sortByArtist.add(song.artist)
            if(!sortByLocation.contains(song.artist)) sortByLocation.add(song.folderPath)
            if(!sortByAlbum.contains(song.artist)) sortByAlbum.add(song.album)
        }
    }

    fun updateList(position: Int) {
        when(selectMode){
            sortMode-> {createList(sortBy,position); selectMode=songMode}
            songMode->{
                val update=list.value!!
                update[position].isSelected=!update[position].isSelected
                list.value=update
            }
        }
    }

    fun onBackPressed() {
        selectMode=sortMode
        createList(sortBy,-1)
    }

}
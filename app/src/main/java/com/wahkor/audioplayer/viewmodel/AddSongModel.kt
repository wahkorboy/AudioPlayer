package com.wahkor.audioplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.audioplayer.model.SelectedList
import com.wahkor.audioplayer.model.Song
import java.util.ArrayList

class AddSongModel : ViewModel() {
    //constant
    private val sortMode = 0
    private val songMode = 1
    private val resultMode = 2
    private val byArtist = "Artist"
    private val byLocation = "Location"
    private val byAlbum = "Album"
    private val byResult = "Result"

    var collectedSong = ArrayList<Song>()
    var chooseSong = ArrayList<Song>() // save to playlist
    val list = MutableLiveData<ArrayList<SelectedList>>()

    // raw data
    private var rawPlaylist = ArrayList<Song>()
    private var sortByArtist = ArrayList<String>()
    private var sortByLocation = ArrayList<String>()
    private var sortByAlbum = ArrayList<String>()

    // insert mode
    private var selectMode = sortMode
    private var sortBy = byArtist

    val toast = MutableLiveData<String>()
    fun build(playlist: ArrayList<Song>, sort: String = "Artist") {
        rawPlaylist = playlist
        sortSetup()
        createList(sort)
    }

    fun createList(sort: String, index: Int = -1) {
        sortBy = sort
        val preList = ArrayList<SelectedList>()
        when (index) {
            -1 -> {
                selectMode = sortMode
                when (sort) {
                    byArtist -> {
                        sortByArtist.forEach { item -> preList.add(SelectedList(false, item)) }
                    }
                    byLocation -> {
                        sortByLocation.forEach { item -> preList.add(SelectedList(false, item)) }
                    }
                    byAlbum -> {
                        sortByAlbum.forEach { item -> preList.add(SelectedList(false, item)) }
                    }

                    byResult -> {
                        selectMode = resultMode
                        sortBy = byResult
                        toast.value="this is result"
                        chooseSong.forEach { song ->
                            preList.add(SelectedList(false, song.title))

                        }
                    }
                }
            }

            else -> {
                collectedSong.clear()
                when (sort) {
                    byArtist -> {
                        val artist = sortByArtist[index]
                        rawPlaylist.forEach { song ->
                            if (song.artist == artist) {
                                val isSelected = chooseSong.contains(song)
                                collectedSong.add(song)
                                preList.add(SelectedList(isSelected, song.title))
                            }
                        }
                    }
                    byLocation -> {
                        val path = sortByLocation[index]
                        rawPlaylist.forEach { song ->
                            if (song.folderPath == path) {
                                val isSelected = chooseSong.contains(song)
                                collectedSong.add(song)
                                preList.add(SelectedList(isSelected, song.title))
                            }
                        }
                    }
                    byAlbum -> {
                        val album = sortByAlbum[index]
                        rawPlaylist.forEach { song ->
                            if (song.album == album) {
                                val isSelected = chooseSong.contains(song)
                                collectedSong.add(song)
                                preList.add(SelectedList(isSelected, song.title))
                            }
                        }
                    }
                }
            }
        }
        list.value = preList

    }

    private fun sortSetup() {
        rawPlaylist.forEach { song: Song ->
            if (!sortByArtist.contains(song.artist)) sortByArtist.add(song.artist)
            if (!sortByLocation.contains(song.folderPath)) sortByLocation.add(song.folderPath)
            if (!sortByAlbum.contains(song.album)) sortByAlbum.add(song.album)
        }
    }

    fun updateList(position: Int) {
        when (selectMode) {
            sortMode -> {
                createList(sortBy, position); selectMode = songMode
            }
            songMode -> {
                val update = list.value!!
                update[position].isSelected = !update[position].isSelected
                list.value = update
                getCollectSong()
            }
        }
    }

    fun onBackPressed() {
        selectMode = sortMode
        createList(sortBy, -1)
    }

    private fun getCollectSong() {
        if (selectMode == sortMode || list.value == null) return
        for (i in 0 until list.value!!.size) {
            if (list.value!![i].isSelected) {
                val newSong = collectedSong[i]
                var unselected = true
                chooseSong.forEach { song ->
                    if (song.data == newSong.data) unselected = false
                }
                if (unselected) chooseSong.add(newSong)
            } else {
                val newSong = collectedSong[i]
                var selected = false
                chooseSong.forEach { song ->
                    if (song.data == newSong.data) selected = true
                }
                if (selected) chooseSong.remove(newSong)

            }
        }
        toast.value = chooseSong.size.toString()

    }
}
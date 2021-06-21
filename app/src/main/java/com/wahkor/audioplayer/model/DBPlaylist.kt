package com.wahkor.audioplayer.model

data class DBPlaylist(
    var playlist:ArrayList<Song>,
    var song:Song,
    var tableName:String,
    var position:Int,
    var allTable:ArrayList<String>,
)

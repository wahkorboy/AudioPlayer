package com.wahkor.audioplayer.model

data class PlayerInfo(
    var playlist:ArrayList<Song>,
    var song:Song,
    var tableName:String,
    var mediaState:Int,
    var position:Int,
)

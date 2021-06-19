package com.wahkor.audioplayer.model

data class PlayerInfo(
    val playlist:ArrayList<Song>,
    val song:Song,
    val tableName:String,
    var mediaState:Int,
    val position:Int,
)

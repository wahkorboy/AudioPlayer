package com.wahkor.audioplayer.model

data class PlayerState(
    val title:CharSequence,
    var duration:Int,
    var current:Int,
    var tvPass:String,
    val tvDue:String,
    val playBTN:Int
)
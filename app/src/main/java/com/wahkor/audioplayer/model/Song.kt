package com.wahkor.audioplayer.model

data class Song(
    val album :String,
    val artist:String,
    val data:String,
    val duration:Long,
    var isPlaying:Boolean,
    val title:String
)
{
    val folderPath:String
    get() {
        return data.substringBeforeLast("/")
    }
    val folderName:String
        get() {
            val folder = data.substringBeforeLast("/")
            return folder.substringAfterLast("/")
        }
}
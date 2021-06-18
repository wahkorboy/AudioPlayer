package com.wahkor.audioplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.wahkor.audioplayer.Constants.COMMAND_NEXT
import com.wahkor.audioplayer.Constants.COMMAND_PREV
import com.wahkor.audioplayer.service.AudioService

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val btn= intent?.action
        btn?.let {
            val audioService=AudioService()
            when(it){
                "prev" ->audioService.controlCommand(COMMAND_PREV){}
                "play" -> audioService.playPauseBTN()
                "next" ->audioService.controlCommand(COMMAND_NEXT){}
                else -> {}
            }

        }
    }
}
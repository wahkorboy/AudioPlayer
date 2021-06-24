package com.wahkor.audioplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.wahkor.audioplayer.helper.Constants.COMMAND_NEXT
import com.wahkor.audioplayer.helper.Constants.COMMAND_PLAY
import com.wahkor.audioplayer.helper.Constants.COMMAND_PREV
import com.wahkor.audioplayer.service.AudioService

class NotificationReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action?:""
        val serviceIntent=Intent(context,AudioService::class.java)
        when(action){
            COMMAND_PLAY->{
                serviceIntent.putExtra("action",action)
            }
            COMMAND_PREV->{
                serviceIntent.putExtra("action",action)
            }
            COMMAND_NEXT->{
                serviceIntent.putExtra("action",action)
            }
        }
        serviceIntent.action="BroadcastReceiver"
        context?.startService(serviceIntent)
    }

}
package com.wahkor.audioplayer.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.os.IBinder
import android.os.PowerManager
import android.widget.Toast
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.helper.Constants.CHANNEL_ID
import com.wahkor.audioplayer.helper.Constants.MUSIC_NOTIFICATION_ID

class TestService: Service() {
    private lateinit var mediaSession:MediaSession
    private var mediaSessionManager:MediaSessionManager?=null
    private lateinit var mediaController: MediaController
    val actionPlay="Action_Play"
    val actionPause="Action_Pause"
    val actionRewind="Action_Rewind"
    val actionPrevious="Action_Previous"
    val actionFastForward="Action_Fast_Forward"
    val actionNext="Action_Next"
    val actionStop="Action_Stop"

    private val mediaPlayer=MediaPlayer()
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun unbindService(conn: ServiceConnection) {
        mediaSession.release()
        super.unbindService(conn)
    }
    private fun handleIntent(intent: Intent?){
        if (intent==null || intent.action==null) return
        when(intent.action!!){
            actionPlay->{
                mediaController.transportControls.play()
            }
            actionPause->{
                mediaController.transportControls.pause()
            }
            actionStop->{
                mediaController.transportControls.stop()
            }
            actionFastForward->{
                mediaController.transportControls.fastForward()
            }
            actionNext->{
                mediaController.transportControls.skipToNext()
            }
            actionRewind->{
                mediaController.transportControls.rewind()
            }
            actionPrevious->{
                mediaController.transportControls.skipToPrevious()
            }
        }
    }
    private fun generateAction(icon:Int,title:String,intentAction:String):Notification.Action{
        val intent=Intent(applicationContext,TestService::class.java)
        intent.action=intentAction
        val pendingIntent=PendingIntent.getService(applicationContext,1,intent,0)
        return Notification.Action.Builder(icon,title,pendingIntent).build()
    }
    private fun buildNotification(action:Notification.Action){
        val style=Notification.MediaStyle()
        val intent=Intent(applicationContext,TestService::class.java)
        intent.action=actionStop
        val pendingIntent=PendingIntent.getService(applicationContext,1,intent,0)
        val builder=Notification.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .setContentTitle("Lock Screen test")
            .setContentText("iryu")
            .setDeleteIntent(pendingIntent)
            .setStyle(style)
            .addAction(generateAction(R.drawable.ic_baseline_skip_previous_24,"previous",actionPrevious))
            .addAction(action)
            .addAction(generateAction(R.drawable.ic_baseline_skip_next_24,"next",actionNext))

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(MUSIC_NOTIFICATION_ID,builder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (mediaSessionManager==null) {
            iniMediaSession()
        }
        Toast.makeText(applicationContext,intent!!.action,Toast.LENGTH_LONG).show()
        handleIntent(intent)
        return START_STICKY
        //return super.onStartCommand(intent, flags, startId)
    }

    private fun iniMediaSession() {
        mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mediaSession= MediaSession(applicationContext,"Test LockScreen")
        mediaController= MediaController(applicationContext,mediaSession.sessionToken)
        mediaSession.setCallback(mediaControlCallback)
    }
    private val mediaControlCallback=object :MediaSession.Callback(){
        override fun onPlay() {
            super.onPlay()
            buildNotification(generateAction(R.drawable.ic_baseline_pause_24,"Pause",actionPause))
        }

        override fun onPause() {
            super.onPause()
            buildNotification(generateAction(R.drawable.ic_baseline_play_arrow_24,"Play",actionPlay))
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            buildNotification(generateAction(R.drawable.ic_baseline_pause_24,"Pause",actionPause))
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            buildNotification(generateAction(R.drawable.ic_baseline_pause_24,"Pause",actionPause))
        }

        override fun onStop() {
            super.onStop()
            val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(MUSIC_NOTIFICATION_ID)
            val intent=Intent(applicationContext,TestService::class.java)
            stopService(intent)
        }
    }
}
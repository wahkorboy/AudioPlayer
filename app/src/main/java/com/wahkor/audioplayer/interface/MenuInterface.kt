package com.wahkor.audioplayer.`interface`

import android.content.Context
import android.content.Intent
import android.widget.PopupMenu
import android.widget.Toast
import com.wahkor.audioplayer.*
import com.wahkor.audioplayer.helper.PlayListManagerActivity

interface MenuInterface {
    fun setOnSettingClick(context: Context,popupMenu: PopupMenu,callback:(Intent) -> Unit){
        popupMenu.inflate(R.menu.setting)
        popupMenu.setOnMenuItemClickListener {
            when(it.title.toString()){
                "SleepTime" ->{
                    val intent= Intent(context,SleepTimeActivity::class.java)
                    callback(intent)
                }
                "AddPlaylist"->{

                }
                else ->
                    Toast.makeText(context,"${it.title}",Toast.LENGTH_LONG).show()
            }
            true
        }
        popupMenu.show()
    }
    fun setOnMenuClick(context: Context,popupMenu: PopupMenu,tableName:String,callback: (Intent) -> Unit){
        popupMenu.inflate(R.menu.menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.title.toString()){
                "Add Song" ->{
                    val intent=Intent(context,AddSongActivity::class.java)
                    intent.putExtra("tableName",tableName)
                    intent.action="Add"
                    callback(intent)
                }
                "Open playlist"->{
                    val intent=Intent(context, PlayListManagerActivity::class.java)
                    intent.putExtra("tableName",tableName)
                    intent.action="Open"
                    callback(intent)
                }
                "Save playlist as" -> {
                    val intent=Intent(context, PlayListManagerActivity::class.java)
                    intent.putExtra("tableName",tableName)
                    intent.action="Save"
                    callback(intent)
                }
            }
            true
        }
        popupMenu.show()
    }

    fun initial()
}

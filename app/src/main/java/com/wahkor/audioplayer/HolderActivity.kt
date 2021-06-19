package com.wahkor.audioplayer

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.wahkor.audioplayer.model.Song
import com.wahkor.audioplayer.service.AudioService
import kotlinx.android.synthetic.main.activity_holder.*
import kotlinx.android.synthetic.main.fragment_player.*

class HolderActivity : AppCompatActivity() {
    private lateinit var viewModel: PlayerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holder)


        viewModel= ViewModelProvider.AndroidViewModelFactory(Application()).create(PlayerViewModel::class.java)
        //viewModel.retrievePlaylist(this)
        val audioService=AudioService()
        audioService.getPlayerInfo.observe(this,{
            Toast.makeText(this,"new update",Toast.LENGTH_SHORT).show()
            holderTitle.text=it.tableName
        })
       /* supportFragmentManager.beginTransaction().replace(R.id.holderFragment,
            PlayerFragment.newInstance(),"Player").commit()
        holderMenu.setOnClickListener {
            toast("this is menu")
        }*/
        holderSetting.setOnClickListener {
           val intent= Intent(this,PlayerActivity::class.java)
           startActivity(intent)
        }
    }

    private fun toast(s: Any) {
        Toast.makeText(this,s.toString(),Toast.LENGTH_SHORT).show()
    }
}
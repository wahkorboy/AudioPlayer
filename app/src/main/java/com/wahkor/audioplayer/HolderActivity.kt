package com.wahkor.audioplayer

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class HolderActivity : AppCompatActivity() {
    private lateinit var viewModel: PlayerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holder)


        viewModel= ViewModelProvider.AndroidViewModelFactory(Application()).create(PlayerViewModel::class.java)
        //viewModel.retrievePlaylist(this)

       /* supportFragmentManager.beginTransaction().replace(R.id.holderFragment,
            PlayerFragment.newInstance(),"Player").commit()
        holderMenu.setOnClickListener {
            toast("this is menu")
        }*/
    }

    private fun toast(s: Any) {
        Toast.makeText(this,s.toString(),Toast.LENGTH_SHORT).show()
    }
}
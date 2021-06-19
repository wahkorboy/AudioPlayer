package com.wahkor.audioplayer

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.wahkor.audioplayer.databinding.ActivityHolderBinding
import com.wahkor.audioplayer.fragment.MenuFragment
import com.wahkor.audioplayer.service.AudioService

class HolderActivity : AppCompatActivity() {
    private val binding:ActivityHolderBinding by lazy { ActivityHolderBinding.inflate(layoutInflater) }
    private val audioService=AudioService()
    private lateinit var viewModel: PlayerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel= ViewModelProvider.AndroidViewModelFactory(Application()).create(PlayerViewModel::class.java)
        audioService.getPlayerInfo.observe(this,{
            binding.holderTitle.text=it.tableName
        })
        binding.holderSetting.setOnClickListener {
            val intent=Intent(this,PlayerActivity::class.java);startActivity(intent)
        }
        //viewModel.retrievePlaylist(this)

        supportFragmentManager.beginTransaction().replace(R.id.holderFragment,
            PlayerFragment.newInstance(),"Player").commit()
            var i=0
        binding.holderMenu.setOnClickListener {
            if (i==0){
                i=1
                supportFragmentManager.beginTransaction().add(R.id.holderFragment,
                    MenuFragment.newInstance(),"Menu").commit()
                toast("open menu")
            }else{
                i=0
                supportFragmentManager.beginTransaction().remove(
                    PlayerFragment.newInstance()).commit()
                toast("close menu")
            }
        }

    }

    private fun toast(s: Any) {
        Toast.makeText(this,s.toString(),Toast.LENGTH_SHORT).show()
    }
}
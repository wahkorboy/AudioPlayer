package com.wahkor.audioplayer

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.wahkor.audioplayer.adapter.AddSongAdapter
import com.wahkor.audioplayer.database.PlayListDB
import com.wahkor.audioplayer.databinding.ActivityAddSongBinding
import com.wahkor.audioplayer.viewmodel.AddSongModel

class AddSongActivity : AppCompatActivity() {
    private lateinit var adapter: AddSongAdapter
    private lateinit var db: PlayListDB
    private lateinit var binding: ActivityAddSongBinding
    private lateinit var viewModel: AddSongModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddSongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = PlayListDB(this)
        val playlist = db.getData("playlist_default")
        viewModel=ViewModelProvider.AndroidViewModelFactory(Application()).create(AddSongModel::class.java).also {
            it.build(playlist)
        }
        val tabLayout = binding.tabLayoutId
        val recycler=binding.recyclerView

        recycler.layoutManager=LinearLayoutManager(this)
        adapter = AddSongAdapter(viewModel.list.value!!){position->
            viewModel.updateList(position)
        }
        recycler.adapter=adapter
        adapter.notifyDataSetChanged()
        // Add Fragment
        tabLayout.addTab(tabLayout.newTab().setText("Artist"),0)
        tabLayout.addTab(tabLayout.newTab().setText("Location"),1)
        tabLayout.addTab(tabLayout.newTab().setText("Album"),2)

        tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position= tab!!.position
                viewModel.insertBy(position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        viewModel.list.observe(this,{
            adapter.notifyDataSetChanged()
        })
    }

    private fun toast(aa: Any) {
        Toast.makeText(this,aa.toString(),Toast.LENGTH_SHORT).show()

    }
}
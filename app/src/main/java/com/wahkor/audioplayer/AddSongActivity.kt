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
import com.wahkor.audioplayer.viewmodel.SelectedList

class AddSongActivity : AppCompatActivity() {
    private lateinit var adapter: AddSongAdapter
    private lateinit var db: PlayListDB
    private lateinit var binding: ActivityAddSongBinding
    private lateinit var viewModel: AddSongModel
    private lateinit var list:ArrayList<SelectedList>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddSongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = PlayListDB(this)
        val playlist = db.getData("playlist_default")
        viewModel=ViewModelProvider.AndroidViewModelFactory(Application()).create(AddSongModel::class.java).also {it.build(playlist)   }
        list=viewModel.list.value!!
        val tabLayout = binding.tabLayoutId
        val tabAction=binding.tabLayoutAction
        val recycler=binding.recyclerView
        recycler.layoutManager=LinearLayoutManager(this)
        // Add Sort Tab
        tabLayout.addTab(tabLayout.newTab().setText("Artist"),0)
        tabLayout.addTab(tabLayout.newTab().setText("Location"),1)
        tabLayout.addTab(tabLayout.newTab().setText("Album"),2)

        //Add Action Tab
        tabAction.addTab(tabAction.newTab().setText(""),0)
        tabAction.addTab(tabAction.newTab().setText("Cancel"),1)
        tabAction.addTab(tabAction.newTab().setText("OK"),2)

        tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.createList(tab!!.text as String)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        viewModel.list.observe(this,{
            list=it
            adapter = AddSongAdapter(it){position->
                viewModel.updateList(position)
            }
            recycler.adapter=adapter
            adapter.notifyDataSetChanged() }   )
    }

    private fun toast(aa: Any) {
        Toast.makeText(this,aa.toString(),Toast.LENGTH_SHORT).show()

    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }
}
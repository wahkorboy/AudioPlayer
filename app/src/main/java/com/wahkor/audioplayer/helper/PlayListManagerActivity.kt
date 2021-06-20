package com.wahkor.audioplayer.helper

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.wahkor.audioplayer.adapter.TableListAdapter
import com.wahkor.audioplayer.databinding.ActivityPlayListManagerBinding
import com.wahkor.audioplayer.viewmodel.PlaylistManagerModel

class PlayListManagerActivity : AppCompatActivity() {
    private val binding:ActivityPlayListManagerBinding by lazy { ActivityPlayListManagerBinding.inflate(layoutInflater) }
    private lateinit var viewModel:PlaylistManagerModel
    private lateinit var adapter: TableListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val mode=intent.action
       val tableName=intent.getStringExtra("tableName")
        binding.title.text=tableName

        viewModel= ViewModelProvider.AndroidViewModelFactory(Application()).create(PlaylistManagerModel::class.java)
        viewModel.build(this)
        viewModel.tableList.observe(this,{tableList ->
            adapter= TableListAdapter(tableList,tableName!!){
                position ->
                // click item position
            }
            binding.recyclerView.layoutManager= GridLayoutManager(this,2)
            binding.recyclerView.adapter=adapter
            adapter.notifyDataSetChanged()
        })

    }
}
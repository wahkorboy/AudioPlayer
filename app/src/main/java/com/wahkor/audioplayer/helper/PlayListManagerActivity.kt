package com.wahkor.audioplayer.helper

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.wahkor.audioplayer.PlayerActivity
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.`interface`.playlistManagerInterface
import com.wahkor.audioplayer.adapter.TableListAdapter
import com.wahkor.audioplayer.databinding.ActivityPlayListManagerBinding
import com.wahkor.audioplayer.fragment.PlaylistOpen
import com.wahkor.audioplayer.viewmodel.PlaylistManagerModel

class PlayListManagerActivity : AppCompatActivity(){
    private val binding:ActivityPlayListManagerBinding by lazy { ActivityPlayListManagerBinding.inflate(layoutInflater) }
    private lateinit var viewModel:PlaylistManagerModel
    private lateinit var adapter: TableListAdapter
    private var selectedTable=MutableLiveData<String>()
    private var tableName=""
    private var oldtableName=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val mode=intent.action
        oldtableName=intent.getStringExtra("tableName")?:"playlist_default"
        binding.title.text=tableName

        viewModel= ViewModelProvider.AndroidViewModelFactory(Application()).create(PlaylistManagerModel::class.java)
        viewModel.build(this,tableName)
        viewModel.tableList.observe(this,{tableList ->
            adapter= TableListAdapter(tableList){
                position ->
                tableName = if(tableName==tableList[position]){   ""
                }else{
                    tableList[position]
                }
                adapter.notifyDataSetChanged()
                selectedTable.value=tableName
                viewModel.build(this,tableName)
            }
            binding.recyclerView.layoutManager= GridLayoutManager(this,2)
            binding.recyclerView.adapter=adapter
            adapter.notifyDataSetChanged()
        })
        when(mode){
            "Open" ->{
                binding.openLayout.visibility= View.VISIBLE
                selectedTable.observe(this,{
                   if(selectedTable.value=="playlist_default") {
                       binding.OpenDelete.visibility=View.GONE
                       binding.OpenSubmit.visibility=View.VISIBLE
                   }else{
                       binding.OpenDelete.visibility=View.VISIBLE
                       binding.OpenSubmit.visibility=View.VISIBLE
                   }
                    if (selectedTable.value == ""){
                        binding.OpenDelete.visibility=View.GONE
                        binding.OpenSubmit.visibility=View.GONE
                    }
                })
            }
            "Save" ->{
                binding.saveLayout.visibility=View.VISIBLE
                selectedTable.observe(this,{
                    binding.saveEditName.setText(it)
                    binding.saveEditName.addTextChangedListener { text->
                        if (text.toString().length in 3..35){
                            binding.saveSubmit.visibility=View.VISIBLE
                        }else{
                            binding.saveSubmit.visibility=View.GONE

                        }

                    }
                    binding.saveSubmit.setOnClickListener {
                        val result=viewModel.saveSubmit(binding.saveEditName.text.toString(),oldtableName)
                        if(result){
                            onBackPressed()
                        }
                    }
            })
        }

    }}

   private fun cancelBTN(){
       onBackPressed()
   }
    override fun onBackPressed() {
        val intent= Intent(this,PlayerActivity::class.java)
        startActivity(intent)
    }
}
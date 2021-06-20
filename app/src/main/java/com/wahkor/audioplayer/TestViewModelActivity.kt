package com.wahkor.audioplayer

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.wahkor.audioplayer.viewmodel.PlayerActivityModel

class TestViewModelActivity : AppCompatActivity() {
    private lateinit var testbtn:Button
    private lateinit var testshowtext:TextView
    private lateinit var viewModel:PlayerActivityModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_view_model)
        testbtn=findViewById(R.id.testbutton)
        testshowtext=findViewById(R.id.testshowtext)
        viewModel= ViewModelProvider.AndroidViewModelFactory(Application()).create(PlayerActivityModel::class.java)
        viewModel.updateSeekbar()
        viewModel.currentPosition.observe(this,{
            testshowtext.text=it
        })
        testbtn.setOnClickListener { viewModel.updateSeekbar() }
    }
}
package com.wahkor.audioplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wahkor.audioplayer.databinding.ActivityFeatureTestBinding
import com.wahkor.audioplayer.helper.Constants.actionPlay
import com.wahkor.audioplayer.service.MusicBackgroundService


class FeatureTestActivity : AppCompatActivity() {
    private val binding: ActivityFeatureTestBinding by lazy {
        ActivityFeatureTestBinding.inflate(layoutInflater)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val intent= Intent(applicationContext,MusicBackgroundService::class.java)
        intent.action=actionPlay
        startService(intent)

    }


    private fun toast(message: Any) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }

    fun prevBTN(view: View) {

    }

    fun nextBTN(view: View) {
    }

    fun actionBTN(view: View) {
    }

}
package com.wahkor.audioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.wahkor.audioplayer.helper.DBConnect

class FeatureTestActivity : AppCompatActivity() {
    private val db=DBConnect()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_test)
        toast(db.getPlaylist(this).size)
    }
    private fun toast(message:Any){
        Toast.makeText(this,message.toString(),Toast.LENGTH_SHORT).show()
    }
}
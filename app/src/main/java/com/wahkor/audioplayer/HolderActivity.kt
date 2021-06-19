package com.wahkor.audioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_holder.*

class HolderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holder)

        supportFragmentManager.beginTransaction().replace(R.id.holderFragment,
            PlayerFragment.newInstance("p1","p2"),"Player").commit()
        holderMenu.setOnClickListener {
            toast("this is menu")
        }
    }

    private fun toast(s: Any) {
        Toast.makeText(this,s.toString(),Toast.LENGTH_SHORT).show()
    }
}
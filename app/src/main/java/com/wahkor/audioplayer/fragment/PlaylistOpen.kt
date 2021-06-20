package com.wahkor.audioplayer.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.wahkor.audioplayer.R

class PlaylistOpen : Fragment() {
    private var tableName:String?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.playlist_open_fragment, container, false)
        tableName=arguments?.getString("tableName")
        val editText=view.findViewById<EditText>(R.id.fragmentOpenNameEditText)
        tableName?.let {
            editText.setText("hello")
            Toast.makeText(view.context,it,Toast.LENGTH_SHORT).show()}

        return view
    }



}
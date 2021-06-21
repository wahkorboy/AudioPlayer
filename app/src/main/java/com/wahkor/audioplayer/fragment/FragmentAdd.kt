package com.wahkor.audioplayer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.databinding.FragmentAddBinding

class FragmentAdd:Fragment(){
    private lateinit var v:View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v=inflater.inflate(R.layout.fragment_add,container,false)
        return view
    }
}
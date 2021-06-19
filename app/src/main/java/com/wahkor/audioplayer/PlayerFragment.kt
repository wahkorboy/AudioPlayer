package com.wahkor.audioplayer

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment


class PlayerFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_player, container, false)
        val songName=view.findViewById<TextView>(R.id.fragmentSongName)
        songName.text="hello"
        return view
    }

    companion object {
        fun newInstance() =
            PlayerFragment().apply {
                arguments = Bundle()
            }
    }
}
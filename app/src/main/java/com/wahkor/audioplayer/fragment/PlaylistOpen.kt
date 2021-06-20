package com.wahkor.audioplayer.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wahkor.audioplayer.R

class PlaylistOpen : Fragment() {

    companion object {
        fun newInstance() = PlaylistOpen()
    }

    private lateinit var viewModel: PlaylistOpenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.playlist_open_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlaylistOpenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
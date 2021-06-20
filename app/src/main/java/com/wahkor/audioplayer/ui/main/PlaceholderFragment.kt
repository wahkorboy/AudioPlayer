package com.wahkor.audioplayer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.adapter.AddSongAdapter
import com.wahkor.audioplayer.adapter.PlaylistAdapter
import com.wahkor.audioplayer.database.PlayListDB
import com.wahkor.audioplayer.databinding.FragmentAddSongBinding

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentAddSongBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: AddSongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
            val db=PlayListDB(requireContext())
            val playlist=db.getData("playlist_default")
            setPlaylist(playlist)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddSongBinding.inflate(inflater, container, false)
        val root = binding.root
        val recycler = binding.AddSongRecycler
        adapter= AddSongAdapter(pageViewModel.playlist.value!!){ newList ->
            pageViewModel.updatePlaylist(newList)
        }
        recycler.adapter=adapter
        adapter.notifyDataSetChanged()
        pageViewModel.playlist.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })
        pageViewModel.playlist.value?.let { Toast.makeText(requireContext(), it.size,Toast.LENGTH_SHORT).show() }
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
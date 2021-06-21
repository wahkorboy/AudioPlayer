package com.wahkor.audioplayer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wahkor.audioplayer.fragment.FragmentAdd


class AddPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {

        return FragmentAdd()
    }

    override fun getItemCount(): Int {
        return 3
    }
}
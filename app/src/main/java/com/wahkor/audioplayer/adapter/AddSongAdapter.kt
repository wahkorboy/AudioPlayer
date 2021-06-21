package com.wahkor.audioplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.`interface`.CustomItemTouchHelperListener
import com.wahkor.audioplayer.model.SelectedSong
import java.util.*
import kotlin.collections.ArrayList

class AddSongAdapter(
    private var selectedSong: ArrayList<SelectedSong>,
    var callback: (Int) -> Unit)
    :RecyclerView.Adapter<AddSongAdapter.SongVH>() {
    inner class SongVH(itemView:View):RecyclerView.ViewHolder (itemView){
        private val titleView=itemView.findViewById<TextView>(R.id.playlistTitle)
        fun binding() {
            val item=selectedSong[adapterPosition]
            titleView.text=item.song.title
            if (item.isSelected){
                itemView.setBackgroundColor(getColor(itemView.context,R.color.selected_playlist))
            }else{

                itemView.setBackgroundColor(getColor(itemView.context,R.color.unselected_playlist))
            }
            itemView.setOnClickListener { callback(adapterPosition) }
        }


    }

    override fun getItemCount(): Int = selectedSong.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongVH {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.play_list_layout,parent,false)
        return SongVH(view)
    }

    override fun onBindViewHolder(holder: SongVH, position: Int) {
        holder.binding()
    }


}




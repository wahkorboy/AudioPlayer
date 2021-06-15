package com.wahkor.audioplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.audioplayer.R
import com.wahkor.audioplayer.`interface`.CustomItemTouchHelperListener
import com.wahkor.audioplayer.model.Song
import java.util.*
import kotlin.collections.ArrayList

class PlaylistAdapter(
    myList: ArrayList<Song>,
    var callback: ( newList: ArrayList<Song>,action:String) -> Unit
) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>(), CustomItemTouchHelperListener {
    val list = myList

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.playlistTitle)
        fun bind() {
            itemView.setOnClickListener {
                callback( updateList(adapterPosition),"ItemClicked")
                notifyDataSetChanged()

            }
            val song = list[adapterPosition]
            title.text = song.title
            if (song.isPlaying) {
                itemView.setBackgroundColor(getColor(itemView.context, R.color.selected_playlist))
            } else {
                itemView.setBackgroundColor(getColor(itemView.context, R.color.unselected_playlist))


            }
        }

    }
    fun updateList(position: Int):ArrayList<Song>{
        if(list.size==0) return ArrayList()
        var i=0
        while (i<list.size){
            list[i++].isPlaying=false
        }
        if(position>list.size-1){
            list[list.size-1].isPlaying=true
        }else{
            list[position].isPlaying=true

        }

        return list
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistAdapter.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.play_list_layout, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: PlaylistAdapter.ViewHolder, position: Int) {
        holder.bind()
    }

    override fun onItemMove(fromPosition: Int, ToPosition: Int): Boolean {
        Collections.swap(list, fromPosition, ToPosition)
        callback(list ,"ItemMoved")
        notifyItemMoved(fromPosition, ToPosition)
        return true

    }

    override fun onItemDismiss(position: Int) {
       // if (list.size > 1) {
            val isCall = list[position].isPlaying
            list.removeAt(position)
            if (isCall) {
                callback(updateList(position),"ItemRemoved")

           }else{
               callback(list,"ItemRemoved")
            }
        notifyItemRemoved(position)

    }
}
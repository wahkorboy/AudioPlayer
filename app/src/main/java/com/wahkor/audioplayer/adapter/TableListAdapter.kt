package com.wahkor.audioplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.audioplayer.R

class TableListAdapter(val list:ArrayList<String>,val tableName:String,val callback:(position:Int)->Unit):RecyclerView.Adapter<TableListAdapter.VH>() {
    inner class VH(itemView:View):RecyclerView.ViewHolder(itemView) {
        private val title=itemView.findViewById<TextView>(R.id.playlistTitle)
        fun binding() {
            title.text=list[adapterPosition]
            if(list[adapterPosition]==tableName){
                itemView.setBackgroundColor(getColor(itemView.context,R.color.selected_playlist))
            }else{
                itemView.setBackgroundColor(getColor(itemView.context,R.color.unselected_playlist))
            }
            itemView.setOnClickListener { callback(adapterPosition) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.play_list_layout,parent,false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding()
    }

    override fun getItemCount(): Int =list.size
}
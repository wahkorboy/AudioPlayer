package com.wahkor.audioplayer.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.audioplayer.`interface`.CustomItemTouchHelperListener

class GroupTouchHelperCallback(private val listener:CustomItemTouchHelperListener):
    ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags= ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags,0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return (viewHolder.itemViewType==target.itemViewType) &&
            listener.onItemMove(viewHolder.adapterPosition,target.adapterPosition)

    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onItemDismiss(viewHolder.adapterPosition)
    }
}
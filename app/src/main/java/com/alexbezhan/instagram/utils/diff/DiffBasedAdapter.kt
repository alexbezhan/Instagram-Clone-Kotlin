package com.alexbezhan.instagram.utils.diff

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

abstract class DiffBasedAdapter<Item, VH : RecyclerView.ViewHolder>(
        private val itemIdGetter: (Item) -> Any) : RecyclerView.Adapter<VH>() {

    var items: List<Item> = emptyList()
        set(newItems) {
            val result = DiffUtil.calculateDiff(SimpleCallback(field, newItems, itemIdGetter))
            field = newItems
            result.dispatchUpdatesTo(this)
        }

    override fun getItemCount() = items.size
}
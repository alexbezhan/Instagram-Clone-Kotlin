package com.alexbezhan.instagram.screens.common

import androidx.recyclerview.widget.DiffUtil

class SimpleCallback<T>(private val oldItems: List<T>, private val newItems: List<T>,
                        private val itemIdGetter: (T) -> Any) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            itemIdGetter(oldItems[oldItemPosition]) == itemIdGetter(newItems[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition] == newItems[newItemPosition]

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size
}
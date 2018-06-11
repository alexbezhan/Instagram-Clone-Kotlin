package com.alexbezhan.instagram.utils

import android.support.v7.util.DiffUtil

class SimpleCallback<T>(private val oldItems: List<T>, private val newItems: List<T>,
                        private val idGetter: (T) -> Any)
    : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            idGetter(oldItems[oldItemPosition]) == idGetter(newItems[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldItems[oldItemPosition] == newItems[newItemPosition]

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

}
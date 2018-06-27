package com.alexbezhan.instagram.screens.profile

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.screens.common.loadImage
import com.alexbezhan.instagram.screens.common.diff.DiffBasedAdapter

class ProfileImagesAdapter :
        DiffBasedAdapter<String, ProfileImagesAdapter.ViewHolder>({ it }) {

    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false) as ImageView
        return ViewHolder(image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(items[position])
    }
}
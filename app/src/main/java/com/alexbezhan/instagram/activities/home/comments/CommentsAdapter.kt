package com.alexbezhan.instagram.activities.home.comments

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.loadUserPhoto
import com.alexbezhan.instagram.activities.setCommentText
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.utils.diff.DiffBasedAdapter
import kotlinx.android.synthetic.main.comments_item.view.*

class CommentsAdapter : DiffBasedAdapter<Comment, CommentsAdapter.ViewHolder>({ it.id }) {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.comments_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = items[position]
        with(holder.view) {
            user_photo.loadUserPhoto(comment.photo)
            comment_text.setCommentText(comment.username, comment.text)
        }
    }
}
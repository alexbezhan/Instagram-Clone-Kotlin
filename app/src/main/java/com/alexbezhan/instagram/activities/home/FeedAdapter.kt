package com.alexbezhan.instagram.activities.home

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.loadImage
import com.alexbezhan.instagram.activities.loadUserPhoto
import com.alexbezhan.instagram.activities.setCommentText
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.utils.diff.DiffBasedAdapter
import kotlinx.android.synthetic.main.feed_item.view.*

class FeedAdapter(private val listener: Listener)
    : DiffBasedAdapter<FeedPost, FeedAdapter.ViewHolder>({ it.id }) {

    interface Listener {
        fun toggleLike(post: FeedPost)
        fun loadLikes(postId: String, position: Int)
        fun comment(postId: String)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    private val defaultPostLikes = FeedPostLikes(0, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        postLikes += (position to likes)
        notifyItemChanged(position)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = items[position]
        val likes = postLikes[position] ?: defaultPostLikes
        with(holder.view) {
            user_photo_image.loadUserPhoto(post.photo)
            username_text.text = post.username
            post_image.loadImage(post.image)
            if (likes.likesCount == 0) {
                likes_text.visibility = View.GONE
            } else {
                likes_text.visibility = View.VISIBLE
                likes_text.text = "${likes.likesCount} likes"
            }
            caption_text.setCommentText(post.username, post.caption)
            like_image.setOnClickListener { listener.toggleLike(post) }
            comment_image.setOnClickListener{ listener.comment(post.id) }
            like_image.setImageResource(
                    if (likes.likedByUser) R.drawable.ic_likes_active
                    else R.drawable.ic_likes_border)
            listener.loadLikes(post.id, position)
        }
    }
}
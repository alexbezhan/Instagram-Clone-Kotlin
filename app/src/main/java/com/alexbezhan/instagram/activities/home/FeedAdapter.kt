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
        fun loadStats(postId: String, position: Int)
        fun comment(postId: String, uid: String)
        fun showComments(postId: String, uid: String)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var postStats: Map<Int, FeedPostStats> = emptyMap()
    private val defaultPostLikes = FeedPostStats()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    fun updatePostStats(position: Int, stats: FeedPostStats) {
        postStats += (position to stats)
        notifyItemChanged(position)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = items[position]
        val stats = postStats[position] ?: defaultPostLikes
        with(holder.view) {
            user_photo_image.loadUserPhoto(post.photo)
            username_text.text = post.username
            post_image.loadImage(post.image)
            if (stats.likesCount == 0) {
                likes_text.visibility = View.GONE
            } else {
                likes_text.visibility = View.VISIBLE
                likes_text.text = "${stats.likesCount} likes"
            }
            if (stats.commentsCount == 0) {
                comments_count_text.visibility = View.GONE
            } else {
                comments_count_text.visibility = View.VISIBLE
                comments_count_text.text = "View all ${stats.commentsCount} comments"
            }
            comments_count_text.setOnClickListener{ listener.showComments(post.id, post.uid)}
            caption_text.setCommentText(post.username, post.caption)
            like_image.setOnClickListener { listener.toggleLike(post) }
            comment_image.setOnClickListener { listener.comment(post.id, post.uid) }
            like_image.setImageResource(
                    if (stats.likedByUser) R.drawable.ic_likes_active
                    else R.drawable.ic_likes_border)
            listener.loadStats(post.id, position)
        }
    }
}
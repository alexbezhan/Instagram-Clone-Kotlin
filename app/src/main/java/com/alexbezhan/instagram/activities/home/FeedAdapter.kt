package com.alexbezhan.instagram.activities.home

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.loadImage
import com.alexbezhan.instagram.activities.loadUserPhoto
import com.alexbezhan.instagram.activities.showToast
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.utils.SimpleCallback
import kotlinx.android.synthetic.main.feed_item.view.*

class FeedAdapter(private val listener: Listener)
    : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    interface Listener {
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var posts: List<FeedPost> = emptyList()
    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    private val defaultPostLikes = FeedPostLikes(0, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    fun setPosts(posts: List<FeedPost>) {
        val result = DiffUtil.calculateDiff(SimpleCallback(this.posts, posts, { it.id }))
        this.posts = posts
        result.dispatchUpdatesTo(this)
    }

    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        postLikes += (position to likes)
        notifyItemChanged(position)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
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
            caption_text.setCaptionText(post.username, post.caption)
            like_image.setOnClickListener { listener.toggleLike(post.id) }
            like_image.setImageResource(
                    if (likes.likedByUser) R.drawable.ic_likes_active
                    else R.drawable.ic_likes_border)
            listener.loadLikes(post.id, position)
        }
    }

    private fun TextView.setCaptionText(username: String, caption: String) {
        val usernameSpannable = SpannableString(username)
        usernameSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, usernameSpannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                widget.context.showToast("Username is clicked")
            }

            override fun updateDrawState(ds: TextPaint?) {}
        }, 0, usernameSpannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        text = SpannableStringBuilder().append(usernameSpannable).append(" ")
                .append(caption)
        movementMethod = LinkMovementMethod.getInstance()
    }

    override fun getItemCount() = posts.size
}
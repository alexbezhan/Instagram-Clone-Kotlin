package com.alexbezhan.instagram.activities.profile.friends

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.loadUserPhoto
import com.alexbezhan.instagram.models.NotificationId
import com.alexbezhan.instagram.models.Uid
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.diff.SimpleCallback
import kotlinx.android.synthetic.main.add_friends_item.view.*

class AddFriendsAdapter(private val listener: Listener)
    : RecyclerView.Adapter<AddFriendsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun toggleFollow(uid: String)
    }

    private var mUsers = listOf<User>()
    private var mPositions = mapOf<String, Int>()
    private var mFollows = mapOf<Uid, NotificationId>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.add_friends_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.view) {
            val user = mUsers[position]
            photo_image.loadUserPhoto(user.photo)
            username_text.text = user.username
            name_text.text = user.name

            View.OnClickListener { listener.toggleFollow(user.uid) }.apply {
                follow_profile_btn.setOnClickListener(this)
                unfollow_btn.setOnClickListener(this)
            }

            val follows = mFollows[user.uid] != null
            if (follows) {
                follow_profile_btn.visibility = View.GONE
                unfollow_btn.visibility = View.VISIBLE
            } else {
                follow_profile_btn.visibility = View.VISIBLE
                unfollow_btn.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = mUsers.size

    fun update(users: List<User>, follows: Map<Uid, NotificationId>) {
        val result = DiffUtil.calculateDiff(SimpleCallback(this.mUsers, users) { it.uid })
        mUsers = users
        mPositions = users.withIndex().map { (idx, user) -> user.uid to idx }.toMap()
        mFollows = follows
        result.dispatchUpdatesTo(this)
    }

}
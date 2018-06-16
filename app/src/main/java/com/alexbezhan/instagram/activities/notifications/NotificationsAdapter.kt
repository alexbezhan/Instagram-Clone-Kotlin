package com.alexbezhan.instagram.activities.notifications

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.loadImage
import com.alexbezhan.instagram.activities.loadUserPhoto
import com.alexbezhan.instagram.activities.setCommentText
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.utils.diff.DiffBasedAdapter
import kotlinx.android.synthetic.main.notification_item.view.*

class NotificationsAdapter(private val listener: Listener) :
        DiffBasedAdapter<Notification, NotificationsAdapter.ViewHolder>({ it.id }) {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun openNotification(notification: Notification)
        fun openProfile(uid: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = items[position]
        val notificationText = with(notification) {
            when (type) {
                NotificationType.FOLLOW -> "started following you"
                NotificationType.LIKE -> "liked your post"
                NotificationType.COMMENT -> "commented: $commentText"
            }
        }

        with(holder.view) {
            user_photo.loadUserPhoto(notification.photo)
            post_image.loadImage(notification.postImage, hideOnNull = true)
            with(View.OnClickListener { listener.openNotification(notification) }) {
                post_image.setOnClickListener(this)
                notification_text.setOnClickListener(this)
            }
            with(View.OnClickListener { listener.openProfile(notification.uid) }) {
                notification_text.setCommentText(notification.username, notificationText,
                        notification.timestampDate(), this)
                user_photo.setOnClickListener(this)
            }
        }
    }
}
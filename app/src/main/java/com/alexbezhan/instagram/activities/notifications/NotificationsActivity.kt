package com.alexbezhan.instagram.activities.notifications

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.BottomNavBar
import com.alexbezhan.instagram.activities.postdetails.PostDetailsActivity
import com.alexbezhan.instagram.activities.profile.ProfileActivity
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
import kotlinx.android.synthetic.main.activity_notifications.*

class NotificationsActivity : BaseActivity(),
        NotificationsAdapter.Listener {
    private val TAG = "NotificationsActivity"

    private lateinit var mAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAuthenticated()) {
            setContentView(R.layout.activity_notifications)
            setupBottomNavigation(BottomNavBar.POSITION_NOTIFICATIONS)
            Log.d(TAG, "onCreate")

            mAdapter = NotificationsAdapter(this)
            notifications_recycler.layoutManager = LinearLayoutManager(this)
            notifications_recycler.adapter = mAdapter

            val model = initModel<NotificationsViewModel>()
            model.notifications.observe(this, Observer {
                it?.let { notifications ->
                    model.checkUnreadNotifications(notifications)
                    mAdapter.items = notifications.sortedByDescending { it.timestampDate() }
                }
            })
        }
    }

    override fun openNotification(notification: Notification) {
        when (notification.type) {
            NotificationType.LIKE, NotificationType.COMMENT -> openPost(notification.postId!!)
            NotificationType.FOLLOW -> openProfile(notification.uid)
        }
    }

    private fun openPost(postId: String) {
        val intent = Intent(this, PostDetailsActivity::class.java)
        intent.putExtra(PostDetailsActivity.EXTRA_POST_ID, postId)
        startActivity(intent)
    }

    override fun openProfile(uid: String) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(ProfileActivity.EXTRA_UID, uid)
        startActivity(intent)
    }
}
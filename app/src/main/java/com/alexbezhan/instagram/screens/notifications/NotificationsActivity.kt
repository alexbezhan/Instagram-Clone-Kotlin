package com.alexbezhan.instagram.screens.notifications

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.screens.common.BaseActivity
import com.alexbezhan.instagram.screens.common.views.BottomNavBar
import com.alexbezhan.instagram.screens.postdetails.PostDetailsActivity
import com.alexbezhan.instagram.screens.profile.ProfileActivity
import com.alexbezhan.instagram.models.Notification
import kotlinx.android.synthetic.main.activity_notifications.*

class NotificationsActivity : BaseActivity(),
        NotificationsAdapter.Listener {
    private val TAG = "NotificationsActivity"

    private lateinit var mAdapter: NotificationsAdapter
    private lateinit var mModel: NotificationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        setupBottomNavigation(BottomNavBar.POSITION_NOTIFICATIONS)
        Log.d(TAG, "onCreate")

        mAdapter = NotificationsAdapter(this)
        notifications_recycler.layoutManager = LinearLayoutManager(this)
        notifications_recycler.adapter = mAdapter

        mModel = initModel(NotificationsViewModelFactory())
        mModel.notifications.observe(this, Observer {
            it?.let { notifications ->
                mModel.onNotifications(notifications)
                mAdapter.items = notifications
            }
        })
        mModel.openPostUiCmd.observe(this, Observer {
            it?.let { postId -> openPost(postId) }
        })
        mModel.openProfileUiCmd.observe(this, Observer {
            it?.let { uid -> openProfile(uid) }
        })
    }

    override fun openNotification(notification: Notification) =
            mModel.openNotification(notification)

    private fun openPost(postId: String) {
        PostDetailsActivity.start(this, postId)
    }

    override fun openProfile(uid: String) {
        ProfileActivity.start(this, uid)
    }
}
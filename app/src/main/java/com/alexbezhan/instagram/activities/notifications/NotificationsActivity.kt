package com.alexbezhan.instagram.activities.notifications

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.BottomNavBar
import com.alexbezhan.instagram.models.Notification
import kotlinx.android.synthetic.main.activity_notifications.*

class NotificationsActivity : BaseActivity(),
        NotificationsAdapter.Listener {
    private val TAG = "NotificationsActivity"

    private lateinit var mAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        setupBottomNavigation(BottomNavBar.POSITION_NOTIFICATIONS)
        Log.d(TAG, "onCreate")

        mAdapter = NotificationsAdapter(this)
        notifications_recycler.layoutManager = LinearLayoutManager(this)
        notifications_recycler.adapter = mAdapter

        val model = initModel<NotificationsViewModel>()
        model.notifications.observe(this, Observer {
            it?.let { mAdapter.items = it }
        })
    }

    override fun openNotification(notification: Notification) {

    }
}
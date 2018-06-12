package com.alexbezhan.instagram.activities.notifications

import android.arch.lifecycle.*
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.*
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.utils.diff.DiffBasedAdapter
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.notification_item.view.*

class NotificationsViewModel : ViewModel() {
    val notifications: LiveData<List<Notification>> = Transformations.map(
            FirebaseLiveData(database.child("notifications").child(currentUid()!!)),
            {
                it.children.map { it.asNotification()!! }
            })
}

class NotificationsActivity : BaseActivity(3), NotificationsAdapter.Listener {
    private val TAG = "NotificationsActivity"

    private lateinit var mAdapter: NotificationsAdapter
    private lateinit var mModel: NotificationsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        setupBottomNavigation()
        Log.d(TAG, "onCreate")

        mAdapter = NotificationsAdapter(this)
        notifications_recycler.layoutManager = LinearLayoutManager(this)
        notifications_recycler.adapter = mAdapter

        mModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        mModel.notifications.observe(this, Observer {
            it?.let {
                mAdapter.items = it
            }
        })
    }

    override fun openNotification(notification: Notification) {

    }
}

class NotificationsAdapter(private val listener: Listener) :
        DiffBasedAdapter<Notification, NotificationsAdapter.ViewHolder>({ it.id }) {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun openNotification(notification: Notification)
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
            notification_text.setCommentText(notification.username, notificationText)
            post_image.loadImage(notification.postImage)
            setOnClickListener { listener.openNotification(notification) }
        }
    }
}
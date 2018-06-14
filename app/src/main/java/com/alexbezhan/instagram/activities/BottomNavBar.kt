package com.alexbezhan.instagram.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.home.HomeActivity
import com.alexbezhan.instagram.activities.notifications.NotificationsActivity
import com.alexbezhan.instagram.activities.profile.ProfileActivity
import com.alexbezhan.instagram.activities.search.SearchActivity
import com.alexbezhan.instagram.activities.share.ShareActivity
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.nhaarman.supertooltips.ToolTip
import com.nhaarman.supertooltips.ToolTipRelativeLayout
import com.nhaarman.supertooltips.ToolTipView
import kotlinx.android.synthetic.main.notifications_tooltip.view.*

class BottomNavBar(private val activity: Activity,
                   private val bnv: BottomNavigationViewEx,
                   private val navPosition: Int) {
    private val TAG = "BottomNavBar"

    private val notificationsImage: ImageView by lazy {
        bnv.getIconAt(POSITION_NOTIFICATIONS)
    }

    private val notificationsContentView: View by lazy {
        val layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as LayoutInflater
        layoutInflater.inflate(R.layout.notifications_tooltip, null, false)
    }

    private var navBarNotification = NavBarNotification()

    private var lastToolTipView: ToolTipView? = null

    fun setupBottomNavigation() {
        bnv.setIconSize(29f, 29f)
        bnv.setTextVisibility(false)
        bnv.enableItemShiftingMode(false)
        bnv.enableShiftingMode(false)
        bnv.enableAnimation(false)
        for (i in 0 until bnv.menu.size()) {
            bnv.setIconTintList(i, null)
        }
        bnv.setOnNavigationItemSelectedListener {
            val nextActivity =
                    when (it.itemId) {
                        R.id.nav_item_home -> HomeActivity::class.java
                        R.id.nav_item_search -> SearchActivity::class.java
                        R.id.nav_item_share -> ShareActivity::class.java
                        R.id.nav_item_likes -> NotificationsActivity::class.java
                        R.id.nav_item_profile -> ProfileActivity::class.java
                        else -> {
                            Log.e(TAG, "unknown nav item clicked $it")
                            null
                        }
                    }
            if (nextActivity != null) {
                val intent = Intent(activity, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                activity.startActivity(intent)
                activity.overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }
    }

    fun setCurrentNavItemActive() {
        bnv.menu.getItem(navPosition).isChecked = true
    }

    fun showNotificationPopover(toolTipLayout: ToolTipRelativeLayout) {
        navBarNotification.apply {
            if (commentsCount > 0 || followersCount > 0 || likesCount > 0) {
                if (notificationsContentView.parent != null) {
                    (notificationsContentView.parent as ViewGroup).removeView(notificationsContentView)
                    lastToolTipView?.remove()
                }

                notificationsContentView.likes_count_text.text = likesCount.toString()
                notificationsContentView.followers_count_text.text = followersCount.toString()
                notificationsContentView.comments_count_text.text = commentsCount.toString()

                val toolTip = ToolTip()
                        .withColor(activity.resources.getColor(R.color.red))
                        .withShadow()
                        .withAnimationType(ToolTip.AnimationType.NONE)
                        .withContentView(notificationsContentView)
                lastToolTipView = toolTipLayout.showToolTipForView(toolTip, notificationsImage)
            }
        }
    }

    fun setNotifications(notifications: List<Notification>) {
        val notificationsByType = notifications
                .filter { !it.isRead }
                .groupBy { it.type }
                .mapValues { (_, values) -> values.size }
        navBarNotification = NavBarNotification(
                likesCount = notificationsByType[NotificationType.LIKE] ?: 0,
                followersCount = notificationsByType[NotificationType.FOLLOW] ?: 0,
                commentsCount = notificationsByType[NotificationType.COMMENT] ?: 0)
    }

    companion object {
        const val POSITION_HOME = 0
        const val POSITION_SEARCH = 1
        const val POSITION_SHARE = 2
        const val POSITION_NOTIFICATIONS = 3
        const val POSITION_PROFILE = 4
    }
}

private data class NavBarNotification(val likesCount: Int = 0, val followersCount: Int = 0,
                                      val commentsCount: Int = 0)
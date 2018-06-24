package com.alexbezhan.instagram.activities.notifications

import com.alexbezhan.instagram.SingleLiveEvent
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.repository.Repository

class NotificationsViewModel(private val uid: String,
                             repository: Repository) : BaseViewModel(repository) {

    val openPostUiCmd = SingleLiveEvent<String>()
    val openProfileUiCmd = SingleLiveEvent<String>()

    fun onNotifications(notifications: List<Notification>) {
        val unreadNotifications = notifications.filter { !it.read }
        if (unreadNotifications.isNotEmpty()) {
            repository.setNotificationsRead(uid, notifications.map { it.id }, true)
                    .addOnFailureListener(setErrorOnFailureListener)
        }
    }

    fun openNotification(notification: Notification) {
        when (notification.type) {
            NotificationType.LIKE, NotificationType.COMMENT ->
                openPostUiCmd.value = notification.postId!!
            NotificationType.FOLLOW -> openProfileUiCmd.value =
                    notification.uid
        }
    }
}
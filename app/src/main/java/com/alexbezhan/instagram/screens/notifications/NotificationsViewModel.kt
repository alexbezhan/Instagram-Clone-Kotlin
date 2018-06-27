package com.alexbezhan.instagram.screens.notifications

import com.alexbezhan.instagram.data.live.SingleLiveEvent
import com.alexbezhan.instagram.screens.common.BaseViewModel
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.data.Repository

class NotificationsViewModel(repository: Repository) : BaseViewModel(repository) {

    val openPostUiCmd = SingleLiveEvent<String>()
    val openProfileUiCmd = SingleLiveEvent<String>()

    fun onNotifications(notifications: List<Notification>) {
        val unreadNotifications = notifications.filter { !it.read }
        if (unreadNotifications.isNotEmpty()) {
            repository.setNotificationsRead(notifications.map { it.id }, true)
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
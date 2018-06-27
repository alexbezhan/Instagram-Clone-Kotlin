package com.alexbezhan.instagram.screens.notifications

import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.data.live.SingleLiveEvent
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.screens.common.CommonLiveData

class NotificationsViewModel(private val repository: Repository, liveData: CommonLiveData) : ViewModel(),
        CommonLiveData by liveData {

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
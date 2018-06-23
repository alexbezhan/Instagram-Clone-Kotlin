package com.alexbezhan.instagram.activities.notifications

import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database

class NotificationsViewModel : BaseViewModel() {
    fun checkUnreadNotifications(notifications: List<Notification>) {
        val unreadNotifications = notifications.filter { !it.read }
        if (unreadNotifications.isNotEmpty()) {
            val updatesMap = unreadNotifications.map { "/${it.id}/read" to true }.toMap()
            database.child("notifications").child(currentUid()!!).updateChildren(updatesMap)
                    .addOnFailureListener(setErrorOnFailureListener)
        }
    }
}
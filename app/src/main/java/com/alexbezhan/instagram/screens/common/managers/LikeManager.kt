package com.alexbezhan.instagram.screens.common.managers

import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks

class LikeManager(private val repository: Repository) {
    fun toggleLike(currentUser: User, post: FeedPost, onFailureListener: OnFailureListener) {
        repository.getLikeValue(post.id, currentUser.uid).onSuccessTask { notificationId ->
            if (notificationId != null) {
                Tasks.whenAll(
                        repository.removeNotification(post.uid, notificationId),
                        repository.deleteLikeValue(post.id, currentUser.uid))
            } else {
                val notification = Notification.like(currentUser, post)
                repository.addNotification(post.uid, notification).onSuccessTask { newNotificationId ->
                    repository.setLikeValue(post.id, currentUser.uid, newNotificationId!!)
                }
            }
        }.addOnFailureListener(onFailureListener)
    }
}
package com.alexbezhan.instagram.activities.home

import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks

interface LikeManager {
    fun toggleLike(currentUser: User, post: FeedPost, onFailureListener: OnFailureListener)
}

class FirebaseLikeManager(private val repository: Repository) : LikeManager {
    override fun toggleLike(currentUser: User, post: FeedPost, onFailureListener: OnFailureListener) {
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
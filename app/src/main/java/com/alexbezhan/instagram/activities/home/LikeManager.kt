package com.alexbezhan.instagram.activities.home

import com.alexbezhan.instagram.models.ToggleNotificationResult
import com.alexbezhan.instagram.models.ToggleType
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
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
                repository.removeNotification(post.uid, notificationId).onSuccessTask {
                    Tasks.forResult(ToggleNotificationResult(notificationId, ToggleType.REMOVED))
                }
            } else {
                val notification = Notification(
                        uid = currentUser.uid,
                        photo = currentUser.photo,
                        username = currentUser.username,
                        type = NotificationType.LIKE,
                        postId = post.id,
                        postImage = post.image
                )
                repository.addNotification(post.uid, notification).onSuccessTask { id ->
                    Tasks.forResult(ToggleNotificationResult(id!!, ToggleType.ADDED))
                }
            }
        }.onSuccessTask { result ->
            when (result!!.toggleType) {
                ToggleType.ADDED -> repository.setLikeValue(post.id, currentUser.uid, result.notificationId)
                ToggleType.REMOVED -> repository.deleteLikeValue(post.id, currentUser.uid)
            }
        }.addOnFailureListener(onFailureListener)
    }
}
package com.alexbezhan.instagram.activities

import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks

interface FollowManager {
    fun toggleFollow(currentUser: User, uid: String, onFailureListener: OnFailureListener)
}

class FirebaseFollowManager(private val repository: Repository) : FollowManager {
    override fun toggleFollow(currentUser: User, uid: String, onFailureListener: OnFailureListener) {
        repository.getUserFollowsValue(currentUser.uid, uid).onSuccessTask { notificationId ->
            if (notificationId != null) {
                Tasks.whenAll(
                        repository.removeNotification(uid, notificationId),
                        repository.deleteFeedPosts(postsAuthorUid = uid, uid = currentUser.uid),
                        repository.deleteUserFollows(currentUser.uid, uid),
                        repository.deleteUserFollowers(uid, currentUser.uid))
            } else {
                val notification = Notification.follow(currentUser)
                repository.addNotification(uid, notification).onSuccessTask { newNotificationId ->
                    Tasks.whenAll(
                            repository.copyFeedPosts(postsAuthorUid = uid, uid = currentUser.uid),
                            repository.setUserFollowsValue(currentUser.uid, uid, newNotificationId!!),
                            repository.setUserFollowersValue(uid, currentUser.uid, newNotificationId))
                }

            }
        }.addOnFailureListener(onFailureListener)
    }

}
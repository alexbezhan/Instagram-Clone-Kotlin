package com.alexbezhan.instagram.activities

import com.alexbezhan.instagram.models.ToggleNotificationResult
import com.alexbezhan.instagram.models.ToggleType
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks

interface FollowManager {
    fun toggleFollow(currentUser: User, uid: String, onFailureListener: OnFailureListener)
}

class FirebaseFollowManager(private val repository: Repository) : FollowManager {
    override fun toggleFollow(currentUser: User, uid: String, onFailureListener: OnFailureListener) {
        repository.getUserFollows(currentUser.uid, uid)
                .onSuccessTask { notificationId ->
                    if (notificationId != null) {
                        repository.removeNotification(uid, notificationId).onSuccessTask {
                            Tasks.forResult(ToggleNotificationResult(notificationId, ToggleType.REMOVED))
                        }
                    } else {
                        val notification = Notification(
                                uid = currentUser.uid,
                                photo = currentUser.photo,
                                username = currentUser.username,
                                type = NotificationType.FOLLOW
                        )
                        repository.addNotification(uid, notification).onSuccessTask { id ->
                            Tasks.forResult(ToggleNotificationResult(id!!, ToggleType.ADDED))
                        }
                    }
                }.onSuccessTask { result ->
                    when (result!!.toggleType) {
                        ToggleType.ADDED ->
                            Tasks.whenAll(
                                    repository.copyFeedPosts(postsAuthorUid = uid, uid = currentUser.uid),
                                    repository.setUserFollowsValue(currentUser.uid, uid, result.notificationId),
                                    repository.setUserFollowersValue(uid, currentUser.uid, result.notificationId))
                        ToggleType.REMOVED ->
                            Tasks.whenAll(
                                    repository.deleteFeedPosts(postsAuthorUid = uid, uid = currentUser.uid),
                                    repository.deleteUserFollows(currentUser.uid, uid),
                                    repository.deleteUserFollowers(uid, currentUser.uid))
                    }
                }.addOnFailureListener(onFailureListener)
    }

}
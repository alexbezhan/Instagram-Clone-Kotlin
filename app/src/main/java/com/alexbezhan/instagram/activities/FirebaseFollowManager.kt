package com.alexbezhan.instagram.activities

import com.alexbezhan.instagram.domain.Notifications
import com.alexbezhan.instagram.domain.ToggleType
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.utils.firebase.ValueEventListenerAdapter
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks

interface FollowManager {
    fun toggleFollow(currentUser: User, uid: String, onFailureListener: OnFailureListener)
}

class FirebaseFollowManager : FollowManager {
    override fun toggleFollow(currentUser: User, uid: String, onFailureListener: OnFailureListener) {
        fun feedPostsTask(follow: Boolean) =
                task<Void> { taskSource ->
                    FirebaseHelper.database.child("feed-posts").child(uid)
                            .orderByChild("uid")
                            .equalTo(uid)
                            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                                val postsMap = if (follow) {
                                    it.children.map { it.key to it.value }.toMap()
                                } else {
                                    it.children.map { it.key to null }.toMap()
                                }
                                FirebaseHelper.database.child("feed-posts")
                                        .child(FirebaseHelper.currentUid()).updateChildren(postsMap)
                                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                            })
                }

        val followsRef = FirebaseHelper.database.child("users").child(FirebaseHelper.currentUid())
                .child("follows").child(uid)
        val followersRef = FirebaseHelper.database.child("users").child(uid).child("followers")
                .child(FirebaseHelper.currentUid())

        Notifications.toggleNotification(currentUser, uid, NotificationType.FOLLOW, followsRef)
                .onSuccessTask { result ->
                    when (result!!.toggleType) {
                        ToggleType.ADDED ->
                            Tasks.whenAll(
                                    feedPostsTask(true),
                                    followsRef.setValue(result.notificationId),
                                    followersRef.setValue(result.notificationId))
                        ToggleType.REMOVED ->
                            Tasks.whenAll(
                                    feedPostsTask(false),
                                    followsRef.removeValue(),
                                    followersRef.removeValue())
                    }
                }.addOnFailureListener(onFailureListener)
    }

}
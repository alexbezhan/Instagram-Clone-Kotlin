package com.alexbezhan.instagram.activities.home

import com.alexbezhan.instagram.domain.Notifications
import com.alexbezhan.instagram.domain.ToggleType
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.google.android.gms.tasks.OnFailureListener

interface LikeManager {
    fun toggleLike(currentUser: User, post: FeedPost, onFailureListener: OnFailureListener)
}

class FirebaseLikeManager : LikeManager {
    override fun toggleLike(currentUser: User, post: FeedPost, onFailureListener: OnFailureListener) {
        val likeRef = FirebaseHelper.database.child("likes").child(post.id).child(currentUser.uid)

        Notifications.toggleNotification(currentUser, post.uid, NotificationType.LIKE, likeRef, post)
                .onSuccessTask { result ->
                    when (result!!.toggleType) {
                        ToggleType.ADDED -> likeRef.setValue(result.notificationId)
                        ToggleType.REMOVED -> likeRef.removeValue()
                    }
                }.addOnFailureListener(onFailureListener)
    }
}
package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.domain.Notifications
import com.alexbezhan.instagram.domain.ToggleType
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData
import com.google.android.gms.tasks.OnFailureListener

interface FeedPostListener {
    fun toggleLike(currentUser: User, post: FeedPost)
    fun observeLikes(postId: String, owner: LifecycleOwner, observer: Observer<FeedPostLikes>)
}

class DefaultFeedPostListener(private val onFailureListener: OnFailureListener) : FeedPostListener {
    private var postLikes = mapOf<String, LiveData<FeedPostLikes>>()

    override fun toggleLike(currentUser: User, post: FeedPost) {
        val likeRef = FirebaseHelper.database.child("likes").child(post.id).child(currentUser.uid)

        Notifications.toggleNotification(currentUser, post.uid, NotificationType.LIKE, post, likeRef)
                .onSuccessTask { result ->
                    when (result!!.toggleType) {
                        ToggleType.ADDED -> likeRef.setValue(result.notificationId)
                        ToggleType.REMOVED -> likeRef.removeValue()
                    }
                }.addOnFailureListener(onFailureListener)
    }

    override fun observeLikes(postId: String, owner: LifecycleOwner, observer: Observer<FeedPostLikes>) {
        val createNewObserver = postLikes[postId] == null
        if (createNewObserver) {
            val data = Transformations.map(FirebaseLiveData(
                    FirebaseHelper.database.child("likes").child(postId)), {
                val userLikes = it.children.map { it.key }.toSet()
                FeedPostLikes(
                        userLikes.size,
                        userLikes.contains(FirebaseHelper.currentUid()))
            })
            data.observe(owner, observer)
            postLikes += (postId to data)
        }
    }
}
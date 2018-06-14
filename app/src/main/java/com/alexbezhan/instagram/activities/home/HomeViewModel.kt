package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.domain.Notifications
import com.alexbezhan.instagram.domain.ToggleType
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

class HomeViewModel : BaseViewModel() {

    private var postLikes = mapOf<String, LiveData<FeedPostLikes>>()

    val feedPosts: LiveData<List<FeedPost>> = Transformations.map(
            FirebaseLiveData(database.child("feed-posts").child(FirebaseHelper.currentUid())),
            {
                it.children
                        .map { it.asFeedPost()!! }
                        .sortedByDescending { it.timestampDate() }
            })

    fun toggleLike(currentUser: User, post: FeedPost) {
        val likeRef = database.child("likes").child(post.id).child(currentUser.uid)

        Notifications.toggleNotification(currentUser, post.uid, NotificationType.LIKE, post, likeRef)
                .onSuccessTask { result ->
                    when (result!!.toggleType) {
                        ToggleType.ADDED -> likeRef.setValue(result.notificationId)
                        ToggleType.REMOVED -> likeRef.removeValue()
                    }
                }.addOnFailureListener(onFailureListener)
    }

    fun observeLikes(postId: String, owner: LifecycleOwner, observer: Observer<FeedPostLikes>) {
        val createNewObserver = postLikes[postId] == null
        if (createNewObserver) {
            val data = Transformations.map(FirebaseLiveData(
                    database.child("likes").child(postId)), {
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
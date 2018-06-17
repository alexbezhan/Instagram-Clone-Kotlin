package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.zipLiveData
import com.alexbezhan.instagram.domain.Notifications
import com.alexbezhan.instagram.domain.ToggleType
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData
import com.google.android.gms.tasks.OnFailureListener

interface FeedPostListener {
    fun toggleLike(currentUser: User, post: FeedPost)
    fun observePostStats(postId: String, owner: LifecycleOwner, observer: Observer<FeedPostStats>)
}

class DefaultFeedPostListener(private val onFailureListener: OnFailureListener) : FeedPostListener {
    private var postStats = mapOf<String, LiveData<FeedPostStats>>()

    override fun toggleLike(currentUser: User, post: FeedPost) {
        val likeRef = FirebaseHelper.database.child("likes").child(post.id).child(currentUser.uid)

        Notifications.toggleNotification(currentUser, post.uid, NotificationType.LIKE, likeRef, post)
                .onSuccessTask { result ->
                    when (result!!.toggleType) {
                        ToggleType.ADDED -> likeRef.setValue(result.notificationId)
                        ToggleType.REMOVED -> likeRef.removeValue()
                    }
                }.addOnFailureListener(onFailureListener)
    }

    override fun observePostStats(postId: String, owner: LifecycleOwner,
                                  observer: Observer<FeedPostStats>) {
        val createNewObserver = postStats[postId] == null
        if (createNewObserver) {
            val likesData = FirebaseLiveData(database.child("likes").child(postId))
            val commentsData = FirebaseLiveData(database.child("comments").child(postId))
            val statsData = Transformations.map(zipLiveData(likesData, commentsData)) { (likesSnapshot, commentsSnapshot) ->
                val userLikes = likesSnapshot.children.map { it.key }.toSet()
                FeedPostStats(
                        likesCount = userLikes.size,
                        commentsCount = commentsSnapshot.children.count(),
                        likedByUser = userLikes.contains(FirebaseHelper.currentUid()))
            }
            statsData.observe(owner, observer)
            postStats += (postId to statsData)
        }
    }
}
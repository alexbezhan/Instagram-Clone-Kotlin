package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.zipLiveData
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

abstract class BaseFeedViewModel(repository: Repository,
                                 private val likeManager: LikeManager) : BaseViewModel(repository) {
    private var postStats = mapOf<String, LiveData<FeedPostStats>>()

    fun toggleLike(currentUser: User, post: FeedPost) {
        likeManager.toggleLike(currentUser, post, setErrorOnFailureListener)
    }

    fun observePostStats(postId: String, owner: LifecycleOwner,
                                  observer: Observer<FeedPostStats>) {
        val createNewObserver = postStats[postId] == null
        if (createNewObserver) {
            val likesData = FirebaseLiveData(database.child("likes").child(postId))
            val commentsData = FirebaseLiveData(database.child("comments").child(postId))
            val statsData = Transformations.map(zipLiveData(likesData, commentsData))
            { (likesSnapshot, commentsSnapshot) ->
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
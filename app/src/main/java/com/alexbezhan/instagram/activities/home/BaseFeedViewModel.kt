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
            val likes = repository.likes(postId)
            val commentsData = repository.commentsCount(postId)
            val statsData = Transformations.map(zipLiveData(likes, commentsData))
            { (likes, commentsCount) ->
                val userLikes = likes.map { it.userId }.toSet()
                FeedPostStats(
                        likesCount = userLikes.size,
                        commentsCount = commentsCount,
                        likedByUser = userLikes.contains(repository.currentUid()))
            }
            statsData.observe(owner, observer)
            postStats += (postId to statsData)
        }
    }
}
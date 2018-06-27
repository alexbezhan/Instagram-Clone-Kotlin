package com.alexbezhan.instagram.screens.home

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.data.live.map
import com.alexbezhan.instagram.data.live.zip
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.screens.common.CommonLiveData
import com.alexbezhan.instagram.screens.common.managers.LikeManager

abstract class BaseFeedViewModel(protected val repository: Repository,
                                 liveData: CommonLiveData) :
        ViewModel(), CommonLiveData by liveData {
    private var postStats = mapOf<String, LiveData<FeedPostStats>>()
    private val likeManager = LikeManager(repository)

    fun toggleLike(currentUser: User, post: FeedPost) {
        likeManager.toggleLike(currentUser, post, setErrorOnFailureListener)
    }

    fun observePostStats(postId: String, owner: LifecycleOwner,
                         observer: Observer<FeedPostStats>) {
        val createNewObserver = postStats[postId] == null
        if (createNewObserver) {
            val likes = repository.likes(postId)
            val commentsData = repository.commentsCount(postId)
            val statsData = likes.zip(commentsData).map { (likes, commentsCount) ->
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
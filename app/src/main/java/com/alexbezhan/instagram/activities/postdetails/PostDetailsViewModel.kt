package com.alexbezhan.instagram.activities.postdetails

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.activities.home.BaseFeedViewModel
import com.alexbezhan.instagram.activities.home.LikeManager
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.repository.Repository

class PostDetailsViewModel(private val uid: String,
                           repository: Repository,
                           likeManager: LikeManager) : BaseFeedViewModel(repository, likeManager) {
    lateinit var post: LiveData<FeedPost>

    fun start(postId: String) {
        post = repository.getFeedPost(uid, postId)
    }
}
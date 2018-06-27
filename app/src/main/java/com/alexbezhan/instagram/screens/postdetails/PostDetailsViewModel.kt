package com.alexbezhan.instagram.screens.postdetails

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.screens.home.BaseFeedViewModel

class PostDetailsViewModel(repository: Repository) : BaseFeedViewModel(repository) {
    lateinit var post: LiveData<FeedPost>

    fun start(postId: String) {
        post = repository.getCurrentUserFeedPost(postId)
    }
}
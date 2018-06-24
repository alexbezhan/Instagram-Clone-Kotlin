package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.activities.map
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.repository.Repository

class HomeViewModel(repository: Repository, likeManager: LikeManager)
    : BaseFeedViewModel(repository, likeManager) {

    val feedPosts: LiveData<List<FeedPost>> =
            repository.getFeedPosts().map {
                it.sortedByDescending { it.timestampDate() }
            }
}
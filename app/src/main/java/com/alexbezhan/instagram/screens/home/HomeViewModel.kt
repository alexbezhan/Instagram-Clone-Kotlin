package com.alexbezhan.instagram.screens.home

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.live.map
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.data.Repository

class HomeViewModel(repository: Repository) : BaseFeedViewModel(repository) {

    val feedPosts: LiveData<List<FeedPost>> =
            repository.getFeedPosts().map {
                it.sortedByDescending { it.timestampDate() }
            }
}
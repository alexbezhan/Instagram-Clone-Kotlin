package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.activities.map
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.repository.Repository

class HomeViewModel(uid: String, repository: Repository) : BaseFeedViewModel() {

    val feedPosts: LiveData<List<FeedPost>> =
            repository.getFeedPosts(uid).map {
                it.sortedByDescending { it.timestampDate() }
            }
}
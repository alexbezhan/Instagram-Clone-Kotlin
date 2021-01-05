package com.alexbezhan.instagram.screens.search

import android.util.Log
import androidx.lifecycle.Observer
import com.alexbezhan.instagram.common.BaseEventListener
import com.alexbezhan.instagram.common.Event
import com.alexbezhan.instagram.common.EventBus
import com.alexbezhan.instagram.data.SearchRepository
import com.alexbezhan.instagram.models.SearchPost

class SearchPostsCreator(searchRepo: SearchRepository) : BaseEventListener() {
    init {
        EventBus.events.observe(this, Observer {
            it?.let { event ->
                when (event) {
                    is Event.CreateFeedPost -> {
                        val searchPost = with(event.post) {
                            SearchPost(
                                    image = image,
                                    caption = caption,
                                    postId = id)
                        }
                        searchRepo.createPost(searchPost).addOnFailureListener {
                            Log.d(TAG, "Failed to create search post for event: $event", it)
                        }
                    }
                    else -> {
                    }
                }
            }
        })
    }

    companion object {
        const val TAG = "SearchPostsCreator"
    }
}
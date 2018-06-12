package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.*
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.activities.setValueTrueOrRemove
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData
import com.alexbezhan.instagram.utils.firebase.ValueEventListenerAdapter

class HomeViewModel : ViewModel() {
    private var postLikes = mapOf<String, LiveData<FeedPostLikes>>()

    val feedPosts: LiveData<List<FeedPost>> = Transformations.map(
            FirebaseLiveData(database.child("feed-posts").child(FirebaseHelper.currentUid())),
            {
                it.children
                        .map { it.asFeedPost()!! }
                        .sortedByDescending { it.timestampDate() }
            })

    fun toggleLike(postId: String) {
        val reference = database.child("likes").child(postId).child(FirebaseHelper.currentUid())
        reference.addListenerForSingleValueEvent(ValueEventListenerAdapter {
            reference.setValueTrueOrRemove(!it.exists())
        })
    }

    fun observeLikes(postId: String, owner: LifecycleOwner, observer: Observer<FeedPostLikes>) {
        val createNewObserver = postLikes[postId] == null
        if (createNewObserver) {
            val data = Transformations.map(FirebaseLiveData(
                    database.child("likes").child(postId)), {
                val userLikes = it.children.map { it.key }.toSet()
                FeedPostLikes(
                        userLikes.size,
                        userLikes.contains(FirebaseHelper.currentUid()))
            })
            data.observe(owner, observer)
            postLikes += (postId to data)
        }
    }
}
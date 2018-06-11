package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.*
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.activities.setValueTrueOrRemove
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.utils.FirebaseLiveData
import com.alexbezhan.instagram.utils.ValueEventListenerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomeViewModel : ViewModel() {
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance().reference
    private val _feedPosts = FirebaseLiveData(database.child("feed-posts").child(uid))
    private var postLikes = mapOf<String, LiveData<FeedPostLikes>>()

    val feedPosts: LiveData<List<FeedPost>> = Transformations.map(_feedPosts,
            { it.children.map { it.asFeedPost()!! } })

    fun toggleLike(postId: String) {
        val reference = database.child("likes").child(postId).child(uid)
        reference.addListenerForSingleValueEvent(ValueEventListenerAdapter {
            reference.setValueTrueOrRemove(!it.exists())
        })
    }

    fun observeLikes(postId: String, owner: LifecycleOwner, observer: Observer<FeedPostLikes>) {
        val createNewObserver = postLikes[postId] == null
        if (createNewObserver) {
            val data = Transformations.map(FirebaseLiveData(database.child("likes").child(postId)), {
                val userLikes = it.children.map { it.key }.toSet()
                FeedPostLikes(
                        userLikes.size,
                        userLikes.contains(uid))
            })
            data.observe(owner, observer)
            postLikes += (postId to data)
        }
    }
}
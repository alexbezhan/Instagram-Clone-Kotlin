package com.alexbezhan.instagram.activities.postdetails

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.activities.home.DefaultFeedPostListener
import com.alexbezhan.instagram.activities.home.FeedPostListener
import com.alexbezhan.instagram.activities.home.FeedPostStats
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

class PostDetailsViewModel : BaseViewModel(), FeedPostListener {
    private val feedPostListener = DefaultFeedPostListener(onFailureListener)

    private lateinit var postId: String
    lateinit var post: LiveData<FeedPost>

    fun start(postId: String) {
        this.postId = postId
        post = Transformations.map(
                FirebaseLiveData(FirebaseHelper.database.child("feed-posts").child(FirebaseHelper.currentUid()!!).child(postId))
        ) {
            it.asFeedPost()!!
        }
    }

    override fun observePostStats(postId: String, owner: LifecycleOwner,
                                  observer: Observer<FeedPostStats>) =
            feedPostListener.observePostStats(postId, owner, observer)

    override fun toggleLike(currentUser: User, post: FeedPost) =
            feedPostListener.toggleLike(currentUser, post)

}
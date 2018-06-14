package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

class HomeViewModel : BaseViewModel(), FeedPostListener {
    private val feedPostListener = DefaultFeedPostListener(onFailureListener)

    val feedPosts: LiveData<List<FeedPost>> = Transformations.map(
            FirebaseLiveData(database.child("feed-posts").child(FirebaseHelper.currentUid())),
            {
                it.children
                        .map { it.asFeedPost()!! }
                        .sortedByDescending { it.timestampDate() }
            })

    override fun observeLikes(postId: String, owner: LifecycleOwner,
                              observer: Observer<FeedPostLikes>) =
            feedPostListener.observeLikes(postId, owner, observer)

    override fun toggleLike(currentUser: User, post: FeedPost) =
            feedPostListener.toggleLike(currentUser, post)

}
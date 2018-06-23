package com.alexbezhan.instagram.activities.postdetails

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.activities.home.BaseFeedViewModel
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

class PostDetailsViewModel : BaseFeedViewModel() {

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
}
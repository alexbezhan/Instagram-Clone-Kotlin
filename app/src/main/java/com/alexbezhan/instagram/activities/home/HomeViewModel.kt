package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

class HomeViewModel : BaseFeedViewModel() {

    val feedPosts: LiveData<List<FeedPost>> = Transformations.map(
            FirebaseLiveData(database.child("feed-posts").child(FirebaseHelper.currentUid()))
    ) {
        it.children
                .map { it.asFeedPost()!! }
                .sortedByDescending { it.timestampDate() }
    }
}
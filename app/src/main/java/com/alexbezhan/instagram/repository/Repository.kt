package com.alexbezhan.instagram.repository

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.activities.map
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

interface Repository {
    fun getFeedPosts(uid: String): LiveData<List<FeedPost>>
}

class FirebaseRepository : Repository {
    override fun getFeedPosts(uid: String): LiveData<List<FeedPost>> =
            FirebaseLiveData(database.child("feed-posts").child(uid)).map {
                it.children.map { it.asFeedPost()!! }
            }
}
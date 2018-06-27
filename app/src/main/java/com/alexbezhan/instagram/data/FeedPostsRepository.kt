package com.alexbezhan.instagram.data

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.models.FeedPost
import com.google.android.gms.tasks.Task

interface FeedPostsRepository {
    fun getFeedPosts(): LiveData<List<FeedPost>>
    fun getFeedPost(uid: String, postId: String): LiveData<FeedPost>
    fun addFeedPost(uid: String, post: FeedPost): Task<Unit>
    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Void>
    fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Void>
    fun getCurrentUserFeedPost(postId: String): LiveData<FeedPost>
}
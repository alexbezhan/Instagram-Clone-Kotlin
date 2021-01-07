package com.alexbezhan.instagram.data

import androidx.lifecycle.LiveData
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.FeedPost
import com.google.android.gms.tasks.Task

interface FeedPostsRepository {
    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun getFeedPost(uid: String, postId: String): LiveData<FeedPost>
    fun getFeedPosts(uid: String): LiveData<List<FeedPost>>
    fun toggleLike(postId: String, uid: String): Task<Unit>
    fun getLikes(postId: String): LiveData<List<FeedPostLike>>
    fun getComments(postId: String): LiveData<List<Comment>>
    fun createComment(postId: String, comment: Comment): Task<Unit>
    fun createFeedPost(uid: String, feedPost: FeedPost): Task<Unit>
}

data class FeedPostLike(val userId: String)
package com.alexbezhan.instagram.screens.home.comments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.screens.common.CommonLiveData
import com.alexbezhan.instagram.screens.common.managers.CommentManager


class CommentsViewModel(private val repository: Repository, liveData: CommonLiveData) : ViewModel(),
        CommonLiveData by liveData {
    private lateinit var commentManager: CommentManager

    lateinit var comments: LiveData<List<Comment>>
        private set

    lateinit var postAuthor: LiveData<User>
        private set

    lateinit var post: LiveData<FeedPost>
        private set

    fun start(postId: String, postUid: String) {
        comments = repository.getComments(postId)
        post = repository.getFeedPost(uid = postUid, postId = postId)
        postAuthor = repository.getUser(postUid)
        commentManager = CommentManager(postId, repository)
    }

    fun postComment(comment: String, user: User, postAuthor: User, post: FeedPost) {
        commentManager.postComment(comment, user, postAuthor, post, setErrorOnFailureListener)
    }
}
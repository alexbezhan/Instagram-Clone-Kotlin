package com.alexbezhan.instagram.activities.home.comments

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.domain.Notifications
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository


class CommentsViewModel(private val repository: Repository) : BaseViewModel() {
    private lateinit var postId: String
    private lateinit var postUid: String

    lateinit var comments: LiveData<List<Comment>>
        private set

    lateinit var postAuthor: LiveData<User>
        private set

    lateinit var post: LiveData<FeedPost>
        private set

    fun start(postId: String, postUid: String) {
        this.postId = postId
        this.postUid = postUid
        comments = repository.getComments(postId)
        post = repository.getFeedPost(uid = postUid, postId = postId)
        postAuthor = repository.getUser(postUid)
    }

    fun postComment(comment: String, user: User, postAuthor: User, post: FeedPost) {
        if (comment.isNotEmpty()) {
            val commentObj = Comment(uid = user.uid, photo = user.photo, username = user.username,
                    text = comment)
            repository.createComment(postId, commentObj)
                    .onSuccessTask {
                        Notifications.addNotification(user, postAuthor.uid, NotificationType.COMMENT,
                                post, comment)
                    }
                    .addOnFailureListener(setErrorOnFailureListener)
        }
    }
}
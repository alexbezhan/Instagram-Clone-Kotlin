package com.alexbezhan.instagram.activities.home.comments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.domain.Notifications
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData


class CommentsViewModel : BaseViewModel() {
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
        comments = Transformations.map(
                FirebaseLiveData(FirebaseHelper.database.child("comments").child(postId))
        ) {
            it.children.map { it.getValue(Comment::class.java)!! }
        }
        post = Transformations.map(
                FirebaseLiveData(FirebaseHelper.database.child("feed-posts").child(postUid)
                        .child(postId))
        ) {
            it.asFeedPost()!!
        }
        postAuthor = Transformations.map(
                FirebaseLiveData(FirebaseHelper.database.child("users").child(postUid))
        ) {
            it.asUser()!!
        }
    }

    fun postComment(comment: String, user: User, postAuthor: User, post: FeedPost) {
        if (comment.isNotEmpty()) {
            val commentObj = Comment(uid = user.uid, photo = user.photo, username = user.username,
                    text = comment)
            val commentRef = FirebaseHelper.database.child("comments").child(postId).push()
            commentRef.setValue(commentObj).addOnFailureListener(onFailureListener)

            Notifications.toggleNotification(user, postAuthor.uid, NotificationType.COMMENT,
                    commentRef.child("notification"), post, comment)
                    .addOnFailureListener(onFailureListener)
        }
    }
}
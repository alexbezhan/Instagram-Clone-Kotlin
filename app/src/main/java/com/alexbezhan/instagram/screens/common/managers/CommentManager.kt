package com.alexbezhan.instagram.screens.common.managers

import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks

class CommentManager(private val postId: String,
                     private val repository: Repository) {
    fun postComment(comment: String, user: User, postAuthor: User, post: FeedPost,
                    onFailureListener: OnFailureListener) {
        if (comment.isNotEmpty()) {
            val commentObj = Comment(uid = user.uid, photo = user.photo, username = user.username,
                    text = comment)
            val notification = Notification.comment(user, post, comment)
            Tasks.whenAll(
                    repository.createComment(postId, commentObj),
                    repository.addNotification(postAuthor.uid, notification)
            ).addOnFailureListener(onFailureListener)
        }
    }
}
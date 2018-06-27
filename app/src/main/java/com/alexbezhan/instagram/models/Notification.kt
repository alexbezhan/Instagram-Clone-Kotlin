package com.alexbezhan.instagram.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class Notification(val uid: String = "", val photo: String? = null,
                        val username: String = "",
                        val type: NotificationType = NotificationType.LIKE,
                        val read: Boolean = false,
                        val postId: String? = null, val postImage: String? = null,
                        val commentText: String? = null,
                        val timestamp: Any = ServerValue.TIMESTAMP,
                        @get:Exclude val id: String = "") {
    fun timestampDate(): Date = Date(timestamp as Long)

    companion object {
        fun comment(commentAuthor: User, post: FeedPost, comment: String) =
                Notification(
                        uid = commentAuthor.uid,
                        photo = commentAuthor.photo,
                        username = commentAuthor.username,
                        type = NotificationType.COMMENT,
                        postId = post.id,
                        postImage = post.image,
                        commentText = comment
                )

        fun follow(currentUser: User) =
                Notification(
                        uid = currentUser.uid,
                        photo = currentUser.photo,
                        username = currentUser.username,
                        type = NotificationType.FOLLOW
                )

        fun like(user: User, post: FeedPost) =
                Notification(
                        uid = user.uid,
                        photo = user.photo,
                        username = user.username,
                        type = NotificationType.LIKE,
                        postId = post.id,
                        postImage = post.image
                )
    }
}

enum class NotificationType {
    LIKE, COMMENT, FOLLOW
}
package com.alexbezhan.instagram.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class Notification(val uid: String = "", val photo: String? = null,
                        val username: String = "",
                        val type: NotificationType = NotificationType.LIKE,
                        val isRead: Boolean = false,
                        val postId: String? = null, val postImage: String? = null,
                        val commentText: String? = null,
                        val timestamp: Any = ServerValue.TIMESTAMP,
                        @get:Exclude val id: String = "") {
    fun timestampDate(): Date = Date(timestamp as Long)
}

enum class NotificationType {
    LIKE, COMMENT, FOLLOW
}
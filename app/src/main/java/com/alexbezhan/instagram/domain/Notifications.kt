package com.alexbezhan.instagram.domain

import com.alexbezhan.instagram.activities.task
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.utils.firebase.ValueEventListenerAdapter
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

object Notifications {
    fun toggleNotification(currentUser: User, toUid: String, type: NotificationType,
                           notificationIdRef: DatabaseReference, toPost: FeedPost? = null,
                           commentText: String? = null): Task<ToggleNotificationResult> {
        return task { taskSource ->
            notificationIdRef.addListenerForSingleValueEvent(ValueEventListenerAdapter {
                val result =
                        if (it.exists()) {
                            removeNotification(toUid, asNotificationId(it))
                        } else {
                            addNotification(currentUser, toUid, type, toPost, commentText)
                        }
                result.addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
            })
        }
    }

    private fun getNotificationsRef(uid: String) =
            FirebaseHelper.database.child("notifications").child(uid)

    private fun addNotification(fromUser: User, toUid: String, type: NotificationType,
                                toPost: FeedPost?, commentText: String?)
            : Task<ToggleNotificationResult> {
        val ref = getNotificationsRef(toUid).push()
        val id = ref.key
        val notification = Notification(
                id = id,
                uid = fromUser.uid,
                photo = fromUser.photo,
                username = fromUser.username,
                type = type,
                postId = toPost?.id,
                postImage = toPost?.image,
                commentText = commentText)
        return ref.setValue(notification).onSuccessTask {
            Tasks.forResult(ToggleNotificationResult(id, ToggleType.ADDED))
        }
    }

    private fun removeNotification(toUid: String, id: String): Task<ToggleNotificationResult> {
        val ref = getNotificationsRef(toUid).child(id)
        return ref.removeValue().onSuccessTask {
            Tasks.forResult(ToggleNotificationResult(id, ToggleType.REMOVED))
        }
    }

    private fun asNotificationId(notificationSnapshot: DataSnapshot): String =
            notificationSnapshot.getValue(String::class.java)!!
}

data class ToggleNotificationResult(val notificationId: String, val toggleType: ToggleType)

enum class ToggleType {
    REMOVED, ADDED
}
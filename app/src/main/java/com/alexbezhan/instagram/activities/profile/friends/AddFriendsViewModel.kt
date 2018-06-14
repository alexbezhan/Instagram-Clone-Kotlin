package com.alexbezhan.instagram.activities.profile.friends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.activities.task
import com.alexbezhan.instagram.domain.Notifications
import com.alexbezhan.instagram.domain.ToggleType
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.firebase.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.utils.firebase.ValueEventListenerAdapter
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData
import com.google.android.gms.tasks.Tasks

class AddFriendsViewModel : BaseViewModel() {
    val userAndFriends: LiveData<Pair<User, List<User>>> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.database.child("users")), {
        val uid = FirebaseHelper.currentUid()
        val allUsers = it.children.map { it.asUser()!! }
        val (userList, otherUsersList) = allUsers.partition { it.uid == uid }
        userList.first() to otherUsersList
    })

    fun toggleFollow(currentUser: User, uid: String) {
        fun feedPostsTask(follow: Boolean) =
                task<Void> { taskSource ->
                    database.child("feed-posts").child(uid)
                            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                                val postsMap = if (follow) {
                                    it.children.map { it.key to it.value }.toMap()
                                } else {
                                    it.children.map { it.key to null }.toMap()
                                }
                                database.child("feed-posts")
                                        .child(currentUid()).updateChildren(postsMap)
                                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                            })
                }

        val followsRef = database.child("users").child(currentUid()).child("follows").child(uid)
        val followersRef = database.child("users").child(uid).child("followers").child(currentUid())

        Notifications.toggleNotification(currentUser, uid, NotificationType.FOLLOW, null, followsRef)
                .onSuccessTask { result ->
                    when (result!!.toggleType) {
                        ToggleType.ADDED ->
                            Tasks.whenAll(
                                    feedPostsTask(true),
                                    followsRef.setValue(result.notificationId),
                                    followersRef.setValue(result.notificationId))
                        ToggleType.REMOVED ->
                            Tasks.whenAll(
                                    feedPostsTask(false),
                                    followsRef.removeValue(),
                                    followersRef.removeValue())
                    }
                }.addOnFailureListener(onFailureListener)
    }

}
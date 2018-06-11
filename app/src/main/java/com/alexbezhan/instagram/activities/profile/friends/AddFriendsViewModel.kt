package com.alexbezhan.instagram.activities.profile.friends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.activities.setValueTrueOrRemove
import com.alexbezhan.instagram.activities.task
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.FirebaseHelper
import com.alexbezhan.instagram.utils.FirebaseLiveData
import com.alexbezhan.instagram.utils.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.utils.ValueEventListenerAdapter
import com.google.android.gms.tasks.Tasks

class AddFriendsViewModel : ViewModel() {
    val userAndFriends: LiveData<Pair<User, List<User>>> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.database.child("users")), {
        val uid = FirebaseHelper.currentUid()
        val allUsers = it.children.map { it.asUser()!! }
        val (userList, otherUsersList) = allUsers.partition { it.uid == uid }
        userList.first() to otherUsersList
    })

    private val _followError = MutableLiveData<String>()

    val followError: LiveData<String> = _followError

    fun follow(uid: String) {
        setFollow(uid, true)
    }

    fun unfollow(uid: String) {
        setFollow(uid, false)
    }

    private fun setFollow(uid: String, follow: Boolean) {
        val followsTask = FirebaseHelper.database.child("users").child(FirebaseHelper.currentUid())
                .child("follows").child(uid).setValueTrueOrRemove(follow)
        val followersTask = FirebaseHelper.database.child("users").child(uid).child("followers")
                .child(FirebaseHelper.currentUid()).setValueTrueOrRemove(follow)

        val feedPostsTask = task<Void> { taskSource ->
            FirebaseHelper.database.child("feed-posts").child(uid)
                    .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                        val postsMap = if (follow) {
                            it.children.map { it.key to it.value }.toMap()
                        } else {
                            it.children.map { it.key to null }.toMap()
                        }
                        FirebaseHelper.database.child("feed-posts")
                                .child(FirebaseHelper.currentUid()).updateChildren(postsMap)
                                .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                    })
        }

        Tasks.whenAll(followsTask, followersTask, feedPostsTask).addOnFailureListener {
            it.message?.let { _followError.value = it }
        }
    }

}
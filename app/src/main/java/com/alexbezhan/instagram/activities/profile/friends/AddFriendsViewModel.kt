package com.alexbezhan.instagram.activities.profile.friends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.FirebaseFollowManager
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

class AddFriendsViewModel : BaseViewModel() {
    private val followManager = FirebaseFollowManager()

    val userAndFriends: LiveData<Pair<User, List<User>>> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.database.child("users"))) {
        val uid = FirebaseHelper.currentUid()
        val allUsers = it.children.map { it.asUser()!! }
        val (userList, otherUsersList) = allUsers.partition { it.uid == uid }
        userList.first() to otherUsersList
    }

    fun toggleFollow(currentUser: User, uid: String) =
            followManager.toggleFollow(currentUser, uid, setErrorOnFailureListener)

}
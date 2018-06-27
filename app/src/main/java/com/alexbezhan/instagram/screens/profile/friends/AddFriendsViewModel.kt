package com.alexbezhan.instagram.screens.profile.friends

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.screens.common.BaseViewModel
import com.alexbezhan.instagram.screens.common.managers.FollowManager
import com.alexbezhan.instagram.data.live.map
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.data.Repository

class AddFriendsViewModel(repository: Repository) : BaseViewModel(repository) {
    private val followManager = FollowManager(repository)

    val userAndFriends: LiveData<Pair<User, List<User>>> =
            repository.getUsers().map { allUsers ->
                val (userList, otherUsersList) = allUsers.partition { it.uid == repository.currentUid() }
                userList.first() to otherUsersList
            }

    fun toggleFollow(currentUser: User, uid: String) =
            followManager.toggleFollow(currentUser, uid, setErrorOnFailureListener)
}
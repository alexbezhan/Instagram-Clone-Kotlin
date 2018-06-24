package com.alexbezhan.instagram.activities.profile.friends

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.FollowManager
import com.alexbezhan.instagram.activities.map
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository

class AddFriendsViewModel(uid: String,
                          repository: Repository,
                          private val followManager: FollowManager) : BaseViewModel(repository) {

    val userAndFriends: LiveData<Pair<User, List<User>>> =
            repository.getUsers().map { allUsers ->
                val (userList, otherUsersList) = allUsers.partition { it.uid == uid }
                userList.first() to otherUsersList
            }

    fun toggleFollow(currentUser: User, uid: String) =
            followManager.toggleFollow(currentUser, uid, setErrorOnFailureListener)
}
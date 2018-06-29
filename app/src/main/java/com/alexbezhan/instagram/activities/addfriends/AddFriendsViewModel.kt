package com.alexbezhan.instagram.activities.addfriends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.activities.map
import com.alexbezhan.instagram.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class AddFriendsViewModel(private val repository: AddFriendsRepository) : ViewModel() {
    val userAndFriends: LiveData<Pair<User, List<User>>> =
            repository.getUsers().map { allUsers ->
                val (userList, otherUsersList) = allUsers.partition {
                    it.uid == repository.currentUid()
                }
                userList.first() to otherUsersList
            }

    fun setFollow(currentUid: String, uid: String, follow: Boolean): Task<Void> {
        return if (follow) {
            Tasks.whenAll(
                    repository.addFollow(currentUid, uid),
                    repository.addFollower(currentUid, uid),
                    repository.copyFeedPosts(postsAuthorUid = uid, uid = currentUid))
        } else {
            Tasks.whenAll(
                    repository.deleteFollow(currentUid, uid),
                    repository.deleteFollower(currentUid, uid),
                    repository.deleteFeedPosts(postsAuthorUid = uid, uid = currentUid))
        }
    }
}
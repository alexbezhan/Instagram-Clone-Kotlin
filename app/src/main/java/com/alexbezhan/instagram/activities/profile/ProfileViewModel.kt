package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.SingleLiveEvent
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.FirebaseFollowManager
import com.alexbezhan.instagram.activities.FollowManager
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository

class ProfileViewModel(uid: String,
                       anotherUid: String?,
                       repository: Repository,
                       private val followManager: FollowManager) : BaseViewModel() {
    var anotherUser: LiveData<User>? = null
    val openEditProfileUiCmd = SingleLiveEvent<Unit>()
    val openProfileSettingsUiCmd = SingleLiveEvent<Unit>()
    val openAddFriendsUiCmd = SingleLiveEvent<Unit>()

    init {
        if (anotherUid != null) {
            anotherUser = repository.getUser(anotherUid)
        }
    }

    val images: LiveData<List<String>> = repository.getImages(uid)

    fun onToggleFollowClick(currentUser: User, uid: String) =
            followManager.toggleFollow(currentUser, uid, setErrorOnFailureListener)

    fun onEditProfileClick() {
        openEditProfileUiCmd.call()
    }

    fun onSettingsClick() {
        openProfileSettingsUiCmd.call()
    }

    fun onAddFriendsClick() {
        openAddFriendsUiCmd.call()
    }
}
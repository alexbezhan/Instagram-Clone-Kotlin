package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.SingleLiveEvent
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.FollowManager
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository

class ProfileViewModel(private val anotherUid: String?,
                       repository: Repository,
                       private val followManager: FollowManager) : BaseViewModel(repository) {
    var anotherUser: LiveData<User>? = null
    val openEditProfileUiCmd = SingleLiveEvent<Unit>()
    val openProfileSettingsUiCmd = SingleLiveEvent<Unit>()
    val openAddFriendsUiCmd = SingleLiveEvent<Unit>()

    init {
        if (isAnotherUser()) {
            anotherUser = repository.getUser(anotherUid!!)
        }
    }

    val images: LiveData<List<String>> =
            if (anotherUid != null) repository.getImages(anotherUid)
            else repository.getImages()

    fun isAnotherUser(): Boolean = anotherUid != null && anotherUid != repository.currentUid()

    fun onToggleFollowClick(currentUser: User) =
            anotherUid?.let {
                followManager.toggleFollow(currentUser, anotherUid, setErrorOnFailureListener)
            }

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
package com.alexbezhan.instagram.screens.profile

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.live.SingleLiveEvent
import com.alexbezhan.instagram.screens.common.BaseViewModel
import com.alexbezhan.instagram.screens.common.managers.FollowManager
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.data.Repository

class ProfileViewModel(private val anotherUid: String?,
                       repository: Repository) : BaseViewModel(repository) {
    var anotherUser: LiveData<User>? = null
    val openEditProfileUiCmd = SingleLiveEvent<Unit>()
    val openProfileSettingsUiCmd = SingleLiveEvent<Unit>()
    val openAddFriendsUiCmd = SingleLiveEvent<Unit>()

    private val followManager = FollowManager(repository)

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
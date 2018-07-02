package com.alexbezhan.instagram.screens.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.common.firebase.FirebaseAuthManager
import com.alexbezhan.instagram.data.firebase.FirebaseFeedPostsRepository
import com.alexbezhan.instagram.data.firebase.FirebaseUsersRepository
import com.alexbezhan.instagram.screens.addfriends.AddFriendsViewModel
import com.alexbezhan.instagram.screens.editprofile.EditProfileViewModel
import com.alexbezhan.instagram.screens.home.HomeViewModel
import com.alexbezhan.instagram.screens.profilesettings.ProfileSettingsViewModel
import com.google.android.gms.tasks.OnFailureListener

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val onFailureListener: OnFailureListener) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(onFailureListener, FirebaseUsersRepository(),
                    FirebaseFeedPostsRepository()) as T
        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(onFailureListener, FirebaseUsersRepository()) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(onFailureListener, FirebaseFeedPostsRepository()) as T
        } else if (modelClass.isAssignableFrom(ProfileSettingsViewModel::class.java)) {
            return ProfileSettingsViewModel(FirebaseAuthManager()) as T
        } else {
            error("Unknown view model class $modelClass")
        }
    }
}
package com.alexbezhan.instagram.activities.profile.friends

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.activities.FirebaseFollowManager
import com.alexbezhan.instagram.repository.FirebaseRepository
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid

@Suppress("UNCHECKED_CAST")
class AddFriendsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddFriendsViewModel(currentUid()!!, FirebaseRepository(),
                FirebaseFollowManager()) as T
    }
}
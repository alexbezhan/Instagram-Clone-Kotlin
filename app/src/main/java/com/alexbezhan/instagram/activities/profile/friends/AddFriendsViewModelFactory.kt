package com.alexbezhan.instagram.activities.profile.friends

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.activities.FirebaseFollowManager
import com.alexbezhan.instagram.repository.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class AddFriendsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddFriendsViewModel(FirebaseRepository(), FirebaseFollowManager()) as T
    }
}
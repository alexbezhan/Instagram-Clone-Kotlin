package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.activities.FirebaseFollowManager
import com.alexbezhan.instagram.repository.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val anotherUid: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(anotherUid, FirebaseRepository(),
                FirebaseFollowManager()) as T
    }
}
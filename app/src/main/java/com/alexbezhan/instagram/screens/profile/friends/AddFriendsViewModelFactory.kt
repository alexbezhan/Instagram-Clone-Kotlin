package com.alexbezhan.instagram.screens.profile.friends

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository
import com.alexbezhan.instagram.screens.common.CommonLiveDataComponent

@Suppress("UNCHECKED_CAST")
class AddFriendsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = FirebaseRepository()
        val liveData = CommonLiveDataComponent(repository)
        return AddFriendsViewModel(repository, liveData) as T
    }
}
package com.alexbezhan.instagram.screens.notifications

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository
import com.alexbezhan.instagram.screens.common.CommonLiveDataComponent

@Suppress("UNCHECKED_CAST")
class NotificationsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = FirebaseRepository()
        val liveData = CommonLiveDataComponent(repository)
        return NotificationsViewModel(repository, liveData) as T
    }
}
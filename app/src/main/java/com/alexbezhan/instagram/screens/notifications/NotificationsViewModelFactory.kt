package com.alexbezhan.instagram.screens.notifications

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class NotificationsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationsViewModel(FirebaseRepository()) as T
    }
}
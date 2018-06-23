package com.alexbezhan.instagram.activities.notifications

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.repository.FirebaseRepository
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid

@Suppress("UNCHECKED_CAST")
class NotificationsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationsViewModel(currentUid()!!, FirebaseRepository()) as T
    }
}
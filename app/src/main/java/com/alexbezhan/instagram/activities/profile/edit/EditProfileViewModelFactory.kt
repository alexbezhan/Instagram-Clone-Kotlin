package com.alexbezhan.instagram.activities.profile.edit

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.repository.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class EditProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditProfileViewModel(FirebaseRepository()) as T
    }
}
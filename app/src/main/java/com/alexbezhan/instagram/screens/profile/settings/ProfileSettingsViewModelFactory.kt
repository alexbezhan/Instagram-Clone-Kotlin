package com.alexbezhan.instagram.screens.profile.settings

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class ProfileSettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileSettingsViewModel(FirebaseRepository()) as T
    }
}
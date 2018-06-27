package com.alexbezhan.instagram.screens.profile.settings

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository
import com.alexbezhan.instagram.screens.common.CommonLiveDataComponent

@Suppress("UNCHECKED_CAST")
class ProfileSettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val liveData = CommonLiveDataComponent(FirebaseRepository)
        return ProfileSettingsViewModel(FirebaseRepository, liveData) as T
    }
}
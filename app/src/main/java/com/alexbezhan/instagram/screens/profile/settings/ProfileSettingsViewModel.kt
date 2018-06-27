package com.alexbezhan.instagram.screens.profile.settings

import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.screens.common.CommonLiveData

class ProfileSettingsViewModel(private val repository: Repository, liveData: CommonLiveData)
    : ViewModel(), CommonLiveData by liveData {

    fun onSignOut() {
        repository.signOut()
    }
}
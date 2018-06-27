package com.alexbezhan.instagram.screens.profile.settings

import com.alexbezhan.instagram.screens.common.BaseViewModel
import com.alexbezhan.instagram.data.Repository

class ProfileSettingsViewModel(repository: Repository) : BaseViewModel(repository) {
    fun onSignOut() {
        repository.signOut()
    }
}
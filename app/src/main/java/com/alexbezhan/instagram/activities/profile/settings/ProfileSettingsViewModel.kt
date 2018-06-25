package com.alexbezhan.instagram.activities.profile.settings

import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.repository.Repository

class ProfileSettingsViewModel(repository: Repository) : BaseViewModel(repository) {
    fun onSignOut() {
        repository.signOut()
    }
}
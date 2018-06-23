package com.alexbezhan.instagram.activities.profile.settings

import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.repository.Repository

class ProfileSettingsViewModel(private val repository: Repository) : BaseViewModel() {
    fun onSignOut() {
        repository.signOut()
    }
}
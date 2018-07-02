package com.alexbezhan.instagram.screens.profilesettings

import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.common.AuthManager

class ProfileSettingsViewModel(private val authManager: AuthManager) : ViewModel(),
        AuthManager by authManager
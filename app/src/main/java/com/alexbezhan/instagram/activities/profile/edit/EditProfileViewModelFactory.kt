package com.alexbezhan.instagram.activities.profile.edit

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.repository.FirebaseRepository
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid

@Suppress("UNCHECKED_CAST")
class EditProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditProfileViewModel(currentUid()!!, FirebaseRepository()) as T
    }
}
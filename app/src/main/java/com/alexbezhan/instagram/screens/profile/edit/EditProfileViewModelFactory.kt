package com.alexbezhan.instagram.screens.profile.edit

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository
import com.alexbezhan.instagram.screens.common.CommonLiveDataComponent

@Suppress("UNCHECKED_CAST")
class EditProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = FirebaseRepository()
        val liveData = CommonLiveDataComponent(repository)
        return EditProfileViewModel(repository, liveData) as T
    }
}
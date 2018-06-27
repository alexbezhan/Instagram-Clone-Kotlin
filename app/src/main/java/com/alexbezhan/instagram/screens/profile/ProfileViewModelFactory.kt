package com.alexbezhan.instagram.screens.profile

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository
import com.alexbezhan.instagram.screens.common.CommonLiveDataComponent

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val anotherUid: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = FirebaseRepository()
        val liveData = CommonLiveDataComponent(repository)
        return ProfileViewModel(anotherUid, repository, liveData) as T
    }
}
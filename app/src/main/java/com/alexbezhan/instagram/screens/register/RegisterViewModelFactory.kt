package com.alexbezhan.instagram.screens.register

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository
import com.alexbezhan.instagram.screens.common.CommonLiveDataComponent

@Suppress("UNCHECKED_CAST")
class RegisterViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = FirebaseRepository()
        val liveData = CommonLiveDataComponent(repository)
        return RegisterViewModel(repository, liveData) as T
    }
}
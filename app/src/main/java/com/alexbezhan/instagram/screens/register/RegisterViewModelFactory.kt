package com.alexbezhan.instagram.screens.register

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class RegisterViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegisterViewModel(FirebaseRepository()) as T
    }
}
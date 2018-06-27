package com.alexbezhan.instagram.screens.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory : ViewModelProvider.Factory  {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(FirebaseRepository()) as T
    }
}
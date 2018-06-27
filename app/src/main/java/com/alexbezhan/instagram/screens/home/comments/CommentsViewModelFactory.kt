package com.alexbezhan.instagram.screens.home.comments

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class CommentsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = FirebaseRepository()
        return CommentsViewModel(repository) as T
    }
}
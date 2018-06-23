package com.alexbezhan.instagram.activities.home.comments

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.repository.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class CommentsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommentsViewModel(FirebaseRepository()) as T
    }
}
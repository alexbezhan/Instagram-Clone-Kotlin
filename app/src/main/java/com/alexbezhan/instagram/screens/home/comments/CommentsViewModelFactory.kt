package com.alexbezhan.instagram.screens.home.comments

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.data.firebase.FirebaseRepository
import com.alexbezhan.instagram.screens.common.CommonLiveDataComponent

@Suppress("UNCHECKED_CAST")
class CommentsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val liveData = CommonLiveDataComponent(FirebaseRepository)
        return CommentsViewModel(FirebaseRepository, liveData) as T
    }
}
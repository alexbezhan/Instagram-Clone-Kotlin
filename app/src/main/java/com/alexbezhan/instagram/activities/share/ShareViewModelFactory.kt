package com.alexbezhan.instagram.activities.share

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.repository.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class ShareViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShareViewModel(FirebaseRepository()) as T
    }
}
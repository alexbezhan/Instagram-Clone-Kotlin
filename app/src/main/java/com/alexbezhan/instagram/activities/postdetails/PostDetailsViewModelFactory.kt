package com.alexbezhan.instagram.activities.postdetails

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.repository.FirebaseRepository
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid

@Suppress("UNCHECKED_CAST")
class PostDetailsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostDetailsViewModel(currentUid()!!, FirebaseRepository()) as T
    }
}
package com.alexbezhan.instagram.activities

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexbezhan.instagram.activities.addfriends.AddFriendsViewModel
import com.alexbezhan.instagram.data.firebase.FirebaseFeedPostsRepository
import com.alexbezhan.instagram.activities.editprofile.EditProfileViewModel
import com.alexbezhan.instagram.data.firebase.FirebaseUsersRepository
import com.google.android.gms.tasks.OnFailureListener

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val onFailureListener: OnFailureListener) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(onFailureListener, FirebaseUsersRepository(),
                    FirebaseFeedPostsRepository()) as T
        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)){
            return EditProfileViewModel(onFailureListener, FirebaseUsersRepository()) as T
        } else {
            error("Unknown view model class $modelClass")
        }
    }
}
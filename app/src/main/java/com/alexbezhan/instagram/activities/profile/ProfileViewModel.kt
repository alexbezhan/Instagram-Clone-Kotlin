package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

class ProfileViewModel : ViewModel() {
    val user: LiveData<User> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.currentUserReference()), { it.asUser()!! })

    val images: LiveData<List<String>> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.database.child("images")
                    .child(FirebaseHelper.currentUid()!!)), {
        it.children.map { it.getValue(String::class.java)!! }
    })
}
package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.FirebaseHelper
import com.alexbezhan.instagram.utils.FirebaseLiveData

class ProfileViewModel : ViewModel() {
    private val _user = FirebaseLiveData(FirebaseHelper.currentUserReference())
    private val _images = FirebaseLiveData(FirebaseHelper.database.child("images")
            .child(FirebaseHelper.currentUid()!!))

    val user: LiveData<User> = Transformations.map(_user, { it.asUser()!! })

    val images: LiveData<List<String>> = Transformations.map(_images, {
        it.children.map { it.getValue(String::class.java)!! }
    })
}
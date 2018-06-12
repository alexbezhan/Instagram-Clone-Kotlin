package com.alexbezhan.instagram.utils.livedata

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper

interface HasUserLiveData {
    val user: LiveData<User>
}

class UserLiveDataComponent : HasUserLiveData {
    override val user: LiveData<User> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.currentUserReference()),
            {
                it.asUser()!!
            })
}
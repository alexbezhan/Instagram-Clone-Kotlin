package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

class ProfileViewModel : BaseViewModel() {
    val images: LiveData<List<String>> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.database.child("images")
                    .child(FirebaseHelper.currentUid()!!)), {
        it.children.map { it.getValue(String::class.java)!! }
    })
}
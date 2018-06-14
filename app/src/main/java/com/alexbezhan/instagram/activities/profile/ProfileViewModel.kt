package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

class ProfileViewModel : BaseViewModel() {
    val images: LiveData<List<String>> = Transformations.map(
            FirebaseLiveData(database.child("images").child(currentUid()!!)),
            {
                it.children.map { it.getValue(String::class.java)!! }
            })
}
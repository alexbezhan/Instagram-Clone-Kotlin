package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.FollowManager
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData

class ProfileViewModel : BaseViewModel() {
    private val followListener = FollowManager()

    var anotherUser: LiveData<User>? = null

    fun setAnotherUid(anotherUid: String) {
        anotherUser = Transformations.map(
                FirebaseLiveData(database.child("users").child(anotherUid)))
        {
            it.asUser()!!
        }
    }

    val images: LiveData<List<String>> = Transformations.map(
            FirebaseLiveData(database.child("images").child(currentUid()!!))
    ) {
        it.children.map { it.getValue(String::class.java)!! }
    }

    fun toggleFollow(currentUser: User, uid: String) =
        followListener.toggleFollow(currentUser, uid, setErrorOnFailureListener)
}
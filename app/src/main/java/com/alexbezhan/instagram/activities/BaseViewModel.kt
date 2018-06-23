package com.alexbezhan.instagram.activities

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData
import com.google.android.gms.tasks.OnFailureListener

abstract class BaseViewModel : ViewModel() {
    private val _errorMessage = MutableLiveData<String>()
    protected val setErrorOnFailureListener = OnFailureListener { setErrorMessage(it.message!!) }
    val error: LiveData<String> = _errorMessage

    val user: LiveData<User> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.currentUserReference()!!)
    ) {
        it.asUser()!!
    }

    val notifications: LiveData<List<Notification>> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.database.child("notifications")
                    .child(FirebaseHelper.currentUid()!!))
    ) {
        Log.d(this.toString(), "notifications: ")
        it.children.map { it.asNotification()!! }
    }

    protected fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
}
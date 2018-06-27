package com.alexbezhan.instagram.utils.firebase

import android.arch.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthStateLiveData : LiveData<String>() {
    private val listener = FirebaseAuth.AuthStateListener {
        value = it.currentUser?.uid
    }

    override fun onActive() {
        super.onActive()
        FirebaseHelper.auth.addAuthStateListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        FirebaseHelper.auth.removeAuthStateListener(listener)
    }
}
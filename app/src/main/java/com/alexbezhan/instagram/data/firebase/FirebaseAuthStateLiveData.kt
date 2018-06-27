package com.alexbezhan.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.firebase.utils.auth
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthStateLiveData : LiveData<String>() {
    private val listener = FirebaseAuth.AuthStateListener {
        value = it.currentUser?.uid
    }

    override fun onActive() {
        super.onActive()
        auth.addAuthStateListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        auth.removeAuthStateListener(listener)
    }
}
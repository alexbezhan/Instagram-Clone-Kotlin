package com.alexbezhan.instagram.common.firebase

import com.alexbezhan.instagram.common.AuthManager
import com.alexbezhan.instagram.data.firebase.common.auth

class FirebaseAuthManager : AuthManager {
    override fun signOut() {
        auth.signOut()
    }
}
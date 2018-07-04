package com.alexbezhan.instagram.common.firebase

import com.alexbezhan.instagram.common.AuthManager
import com.alexbezhan.instagram.common.toUnit
import com.alexbezhan.instagram.data.firebase.common.auth
import com.google.android.gms.tasks.Task

class FirebaseAuthManager : AuthManager {
    override fun signOut() {
        auth.signOut()
    }

    override fun signIn(email: String, password: String): Task<Unit> =
        auth.signInWithEmailAndPassword(email, password).toUnit()
}
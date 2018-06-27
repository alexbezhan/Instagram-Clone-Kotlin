package com.alexbezhan.instagram.data

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.Task

interface AuthRepository {
    fun currentUid(): String?
    fun signIn(email: String, password: String): Task<Unit>
    fun authState(): LiveData<String>
    fun signOut()
}
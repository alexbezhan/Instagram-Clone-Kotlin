package com.alexbezhan.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.AuthRepository
import com.alexbezhan.instagram.data.firebase.utils.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.data.firebase.utils.auth
import com.alexbezhan.instagram.data.task
import com.alexbezhan.instagram.data.toUnit
import com.google.android.gms.tasks.Task

class FirebaseAuthRepository : AuthRepository {

    override fun authState(): LiveData<String> = FirebaseAuthStateLiveData()

    override fun currentUid(): String? = auth.currentUser?.uid

    override fun signOut() = auth.signOut()

    override fun signIn(email: String, password: String): Task<Unit> =
            task { taskSource ->
                auth.signInWithEmailAndPassword(email, password)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
            }

}
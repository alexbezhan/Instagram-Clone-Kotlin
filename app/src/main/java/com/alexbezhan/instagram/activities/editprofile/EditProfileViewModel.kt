package com.alexbezhan.instagram.activities.editprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.alexbezhan.instagram.models.User
import com.google.android.gms.tasks.Task

class EditProfileViewModel(private val repository: EditProfileRepository) : ViewModel() {
    val user: LiveData<User> = repository.getUser()

    fun uploadAndSetUserPhoto(localImage: Uri): Task<Unit> =
            repository.uploadUserPhoto(localImage).onSuccessTask { downloadUrl ->
                repository.updateUserPhoto(downloadUrl!!)
            }

    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> =
            repository.updateEmail(currentEmail = currentEmail, newEmail = newEmail,
                    password = password)

    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> =
            repository.updateUserProfile(currentUser = currentUser, newUser = newUser)
}

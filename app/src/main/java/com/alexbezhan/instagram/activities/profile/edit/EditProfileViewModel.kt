package com.alexbezhan.instagram.activities.profile.edit

import android.net.Uri
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.SingleLiveEvent
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository
import com.google.android.gms.tasks.Task

class EditProfileViewModel(repository: Repository) : BaseViewModel(repository) {

    val openPasswordConfirmDialogCmd = SingleLiveEvent<String>()
    private var pendingUser: User? = null
    val profileSavedEvent = SingleLiveEvent<Unit>()

    fun onImageTaken(photo: Uri): Task<Unit> =
            repository.uploadUserPhoto(photo).onSuccessTask {
                repository.setUserPhotoUrl(it!!)
            }.addOnFailureListener(setErrorOnFailureListener)


    fun onPasswordConfirm(currentUser: User, password: String) {
        if (password.isNotEmpty()) {
            val newUser = pendingUser!!
            repository.updateUserEmail(currentUser.email, newUser.email, password)
                    .onSuccessTask { repository.updateUserProfile(newUser, currentUser) }
                    .addOnSuccessListener { profileSavedEvent.call() }
                    .addOnFailureListener(setErrorOnFailureListener)
        } else {
            setErrorMessage(R.string.you_should_enter_password)
        }
    }

    fun onSaveProfileClick(newUser: User, currentUser: User) {
        val error = validate(newUser)
        if (error == null) {
            if (newUser.email == currentUser.email) {
                repository.updateUserProfile(newUser, currentUser)
                        .addOnSuccessListener { profileSavedEvent.call() }
                        .addOnFailureListener(setErrorOnFailureListener)
            } else {
                pendingUser = newUser
                openPasswordConfirmDialogCmd.call()
            }
        } else {
            setErrorMessage(error)
        }
    }

    private fun validate(user: User): Int? =
            when {
                user.name.isEmpty() -> R.string.please_enter_name
                user.username.isEmpty() -> R.string.please_enter_username
                user.email.isEmpty() -> R.string.please_enter_email
                else -> null
            }
}
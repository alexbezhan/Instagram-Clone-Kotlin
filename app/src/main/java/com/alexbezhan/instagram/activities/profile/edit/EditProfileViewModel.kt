package com.alexbezhan.instagram.activities.profile.edit

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.SingleLiveEvent
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository
import com.google.android.gms.tasks.Task

class EditProfileViewModel(private val uid: String,
                           private val repository: Repository) : BaseViewModel() {

    val openPasswordConfirmDialogCmd = SingleLiveEvent<String>()
    private val _pendingEmail = MutableLiveData<String>()
    val pendingEmail: LiveData<String> = _pendingEmail
    val profileSavedEvent = SingleLiveEvent<Unit>()

    fun uploadAndSetUserPhoto(photo: Uri): Task<Unit> =
            repository.uploadAndSetUserPhoto(uid, photo)
                    .addOnFailureListener(setErrorOnFailureListener)

    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> {
        return repository.updateUserEmail(currentEmail, newEmail, password)
                .addOnFailureListener(setErrorOnFailureListener)
    }

    fun onPasswordConfirm(currentEmail: String, newEmail: String, password: String) {
        if (password.isNotEmpty()) {
            updateEmail(
                    currentEmail = currentEmail,
                    newEmail = newEmail,
                    password = password)
        } else {
            setErrorMessage(R.string.you_should_enter_password)
        }
    }

    fun onSaveProfileClick(newUser: User, currentUser: User) {
        val error = validate(newUser)
        if (error == null) {
            if (newUser.email == currentUser.email) {
                repository.updateUserProfile(uid, newUser)
                        .addOnSuccessListener { profileSavedEvent.call() }
                        .addOnFailureListener(setErrorOnFailureListener)
            } else {
                _pendingEmail.value = newUser.email
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
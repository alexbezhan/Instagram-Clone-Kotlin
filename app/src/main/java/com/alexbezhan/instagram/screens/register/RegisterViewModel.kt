package com.alexbezhan.instagram.screens.register

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.data.live.SingleLiveEvent
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.screens.common.CommonLiveData

class RegisterViewModel(private val repository: Repository, liveData: CommonLiveData)
    : ViewModel(), CommonLiveData by liveData {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    val openNamePassUiCmd = SingleLiveEvent<Unit>()
    val goBackToEmailUiCmd = SingleLiveEvent<Unit>()
    val openHomeUiCmd = SingleLiveEvent<Unit>()

    fun onEmailEntered(email: String) {
        this._email.value = email
        if (email.isNotEmpty()) {
            repository.isUserExistsByEmail(email).addOnSuccessListener { userExists ->
                if (userExists) {
                    openNamePassUiCmd.call()
                } else {
                    setErrorMessage(R.string.email_already_exists)
                }
            }
        } else {
            setErrorMessage(R.string.enter_email)
        }
    }

    fun onRegister(email: String?, fullName: String, password: String) {
        if (fullName.isNotEmpty() && password.isNotEmpty()) {
            if (email != null) {
                repository.createUser(mkUser(fullName, email), password)
                        .addOnFailureListener(setErrorOnFailureListener)
                        .addOnSuccessListener { openHomeUiCmd.call() }
            } else {
                setErrorMessage(R.string.enter_email)
                goBackToEmailUiCmd.call()
            }
        } else {
            setErrorMessage(R.string.please_enter_email_and_password)
        }
    }

    private fun mkUser(fullName: String, email: String): User {
        val username = mkUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }

    private fun mkUsername(fullName: String) =
            fullName.toLowerCase().replace(" ", ".")
}
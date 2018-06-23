package com.alexbezhan.instagram.activities.login

import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.SingleLiveEvent
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.repository.Repository

class LoginViewModel(private val repository: Repository) : BaseViewModel() {

    val openHomeActivityCmd = SingleLiveEvent<Unit>()
    val openRegisterActivityCmd = SingleLiveEvent<Unit>()

    fun onLogin(email: String, password: String) {
        if (validate(email, password)) {
            repository.signIn(email, password)
                    .addOnFailureListener(setErrorOnFailureListener)
                    .addOnSuccessListener { openHomeActivityCmd.call() }
        } else {
            setErrorMessage(R.string.please_enter_email_and_password)
        }
    }

    fun onCreateAccount() {
        openRegisterActivityCmd.call()
    }

    private fun validate(email: String, password: String) =
            email.isNotEmpty() && password.isNotEmpty()


}
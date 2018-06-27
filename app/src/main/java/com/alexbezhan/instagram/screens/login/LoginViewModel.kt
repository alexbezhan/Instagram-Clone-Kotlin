package com.alexbezhan.instagram.screens.login

import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.data.live.SingleLiveEvent
import com.alexbezhan.instagram.screens.common.BaseViewModel
import com.alexbezhan.instagram.data.Repository

class LoginViewModel(repository: Repository) : BaseViewModel(repository) {

    val openHomeUiCmd = SingleLiveEvent<Unit>()
    val openRegisterUiCmd = SingleLiveEvent<Unit>()

    fun onLogin(email: String, password: String) {
        if (validate(email, password)) {
            setLoading(true)
            repository.signIn(email, password)
                    .addOnFailureListener(setErrorOnFailureListener)
                    .addOnSuccessListener { openHomeUiCmd.call() }
                    .addOnCompleteListener { setLoading(false) }
        } else {
            setErrorMessage(R.string.please_enter_email_and_password)
        }
    }

    fun onCreateAccount() {
        openRegisterUiCmd.call()
    }

    private fun validate(email: String, password: String) =
            email.isNotEmpty() && password.isNotEmpty()


}
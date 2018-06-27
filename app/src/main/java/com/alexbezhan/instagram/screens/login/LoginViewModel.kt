package com.alexbezhan.instagram.screens.login

import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.data.live.SingleLiveEvent
import com.alexbezhan.instagram.screens.common.CommonLiveData

class LoginViewModel(private val repository: Repository, liveData: CommonLiveData) : ViewModel(),
        CommonLiveData by liveData {

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
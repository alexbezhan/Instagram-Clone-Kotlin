package com.alexbezhan.instagram.activities

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.StringRes
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository
import com.google.android.gms.tasks.OnFailureListener

abstract class BaseViewModel(protected val repository: Repository) : ViewModel() {
    private val _errorMessage = MutableLiveData<ErrorMessage>()
    protected val setErrorOnFailureListener = OnFailureListener { setErrorMessage(it.message!!) }
    val error: LiveData<ErrorMessage> = _errorMessage

    val user: LiveData<User> = repository.getUser(repository.currentUid()!!)

    val notifications: LiveData<List<Notification>> =
            repository.notifications().map {
                it.sortedByDescending { it.timestampDate() }
            }

    protected fun setErrorMessage(@StringRes resId: Int) {
        _errorMessage.value = ErrorMessage.stringRes(resId)
    }

    protected fun setErrorMessage(message: String) {
        _errorMessage.value = ErrorMessage.plain(message)
    }
}
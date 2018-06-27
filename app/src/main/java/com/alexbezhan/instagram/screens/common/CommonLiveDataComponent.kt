package com.alexbezhan.instagram.screens.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.StringRes
import com.alexbezhan.instagram.data.AuthRepository
import com.alexbezhan.instagram.data.NotificationsRepository
import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.data.UsersRepository
import com.alexbezhan.instagram.data.live.map
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.User
import com.google.android.gms.tasks.OnFailureListener

class CommonLiveDataComponent<Repo>(repository: Repo) : CommonLiveData
        where Repo : AuthRepository, Repo : UsersRepository, Repo : NotificationsRepository {

    private val _errorMessage = MutableLiveData<ErrorMessage>()
    override val setErrorOnFailureListener = OnFailureListener { setErrorMessage(it.message!!) }

    private val _isLoading = MutableLiveData<Boolean>()
    override val isLoading: LiveData<Boolean> = _isLoading
    override val authState: LiveData<String> = repository.authState()
    override val error: LiveData<ErrorMessage> = _errorMessage
    override val user: LiveData<User> = repository.getUser()

    override val notifications: LiveData<List<Notification>> =
            repository.notifications().map {
                it.sortedByDescending { it.timestampDate() }
            }

    override fun setLoading(loading: Boolean) {
        this._isLoading.value = loading
    }

    override fun setErrorMessage(@StringRes resId: Int) {
        _errorMessage.value = ErrorMessage.stringRes(resId)
    }

    override fun setErrorMessage(message: String) {
        _errorMessage.value = ErrorMessage.plain(message)
    }
}

interface CommonLiveData {
    val setErrorOnFailureListener: OnFailureListener
    val isLoading: LiveData<Boolean>
    val authState: LiveData<String>
    val error: LiveData<ErrorMessage>
    val user: LiveData<User>
    val notifications: LiveData<List<Notification>>

    fun setLoading(loading: Boolean)
    fun setErrorMessage(@StringRes resId: Int)
    fun setErrorMessage(message: String)
}
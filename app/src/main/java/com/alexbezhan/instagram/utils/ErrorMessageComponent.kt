package com.alexbezhan.instagram.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener

class ErrorMessageComponent : ProducesErrorMessage {
    private val _errorMessage = MutableLiveData<String>()
    override val errorMessage: LiveData<String> = _errorMessage
    val onFailureListener = OnFailureListener {
        _errorMessage.value = it.message!!
    }
}
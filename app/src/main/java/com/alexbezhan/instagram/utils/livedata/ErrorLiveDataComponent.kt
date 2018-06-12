package com.alexbezhan.instagram.utils.livedata

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener

interface HasErrorLiveData {
    val error: LiveData<String>
}

class ErrorLiveDataComponent : HasErrorLiveData {
    private val _errorMessage = MutableLiveData<String>()
    override val error: LiveData<String> = _errorMessage
    val onFailureListener = OnFailureListener {
        _errorMessage.value = it.message!!
    }
}
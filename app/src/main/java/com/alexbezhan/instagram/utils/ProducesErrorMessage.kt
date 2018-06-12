package com.alexbezhan.instagram.utils

import android.arch.lifecycle.LiveData

interface ProducesErrorMessage {
    val errorMessage: LiveData<String>
}
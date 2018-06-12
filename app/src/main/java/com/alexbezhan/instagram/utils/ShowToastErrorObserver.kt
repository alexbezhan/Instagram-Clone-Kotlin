package com.alexbezhan.instagram.utils

import android.arch.lifecycle.Observer
import android.content.Context
import com.alexbezhan.instagram.activities.showToast

class ShowToastErrorObserver(private val context: Context) : Observer<String> {
    override fun onChanged(message: String?) {
        message?.let { context.showToast(message) }
    }

}
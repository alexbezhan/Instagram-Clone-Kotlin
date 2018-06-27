package com.alexbezhan.instagram.screens.common

import android.support.annotation.StringRes as AndroidStringRes

sealed class ErrorMessage {
    data class Plain(val message: String) : ErrorMessage()
    data class StringRes(@AndroidStringRes val resId: Int) : ErrorMessage()

    companion object {
        fun plain(message: String) = Plain(message)
        fun stringRes(resId: Int) = StringRes(resId)
    }
}
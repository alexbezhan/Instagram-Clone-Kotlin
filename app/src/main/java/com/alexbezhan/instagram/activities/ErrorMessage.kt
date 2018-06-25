package com.alexbezhan.instagram.activities

import android.support.annotation.StringRes as AndroidStringRes

sealed class ErrorMessage {
    data class Plain(val message: String) : ErrorMessage()
    data class StringRes(@AndroidStringRes val resId: Int) : ErrorMessage()

    companion object {
        fun plain(message: String) = ErrorMessage.Plain(message)
        fun stringRes(resId: Int) = ErrorMessage.StringRes(resId)
    }
}
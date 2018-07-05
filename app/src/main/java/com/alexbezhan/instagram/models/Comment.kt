package com.alexbezhan.instagram.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class Comment(val uid: String = "", val username: String = "", val photo: String? = null,
                   val text: String = "", val timestamp: Any = ServerValue.TIMESTAMP,
                   @get:Exclude val id: String = "") {
    fun timestampDate() = Date(timestamp as Long)
}
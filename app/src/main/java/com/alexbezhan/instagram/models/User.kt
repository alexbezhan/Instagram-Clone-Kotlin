package com.alexbezhan.instagram.models

import com.google.firebase.database.Exclude

data class User(val name: String = "", val username: String = "", val email: String = "",
                val follows: Map<Uid, NotificationId> = emptyMap(),
                val followers: Map<Uid, NotificationId> = emptyMap(),
                val website: String? = null, val bio: String? = null, val phone: Long? = null,
                val photo: String? = null, @get:Exclude val uid: String = "")

typealias Uid = String
typealias NotificationId = String
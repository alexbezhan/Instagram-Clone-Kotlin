package com.alexbezhan.instagram.models

import com.google.firebase.database.Exclude

data class Comment(val uid: String = "", val photo: String? = null,
                   val username: String = "", val text: String = "",
                   @get:Exclude val id: String = "")
package com.alexbezhan.instagram.models

data class User(val name: String = "", val username: String = "", val email: String = "",
                val website: String? = null, val bio: String? = null, val phone: Long? = null,
                val photo: String? = null)
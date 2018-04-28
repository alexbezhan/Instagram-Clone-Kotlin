package com.alexbezhan.instagram.models

data class User(val name: String = "", val username: String = "", val website: String = "",
                val bio: String = "", val email: String = "", val phone: Long = 0L)
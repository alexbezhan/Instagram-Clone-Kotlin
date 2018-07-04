package com.alexbezhan.instagram.screens.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.data.UsersRepository

class ProfileViewModel(private val usersRepo: UsersRepository) : ViewModel() {
    val user = usersRepo.getUser()
    lateinit var images: LiveData<List<String>>

    fun init(uid: String) {
        images = usersRepo.getImages(uid)
    }
}
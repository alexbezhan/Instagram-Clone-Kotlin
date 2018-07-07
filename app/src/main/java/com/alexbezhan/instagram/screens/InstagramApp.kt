package com.alexbezhan.instagram.screens

import android.app.Application
import com.alexbezhan.instagram.common.firebase.FirebaseAuthManager
import com.alexbezhan.instagram.data.firebase.FirebaseFeedPostsRepository
import com.alexbezhan.instagram.data.firebase.FirebaseUsersRepository

class InstagramApp : Application() {
    val usersRepo by lazy { FirebaseUsersRepository() }
    val feedPostsRepo by lazy { FirebaseFeedPostsRepository() }
    val authManager by lazy { FirebaseAuthManager() }
}
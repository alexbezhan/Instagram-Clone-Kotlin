package com.alexbezhan.instagram.screens

import android.app.Application
import com.alexbezhan.instagram.common.firebase.FirebaseAuthManager
import com.alexbezhan.instagram.data.firebase.FirebaseFeedPostsRepository
import com.alexbezhan.instagram.data.firebase.FirebaseNotificationsRepository
import com.alexbezhan.instagram.data.firebase.FirebaseSearchRepository
import com.alexbezhan.instagram.data.firebase.FirebaseUsersRepository
import com.alexbezhan.instagram.screens.notifications.NotificationsCreator
import com.alexbezhan.instagram.screens.search.SearchPostsCreator

class InstagramApp : Application() {
    val usersRepo by lazy { FirebaseUsersRepository() }
    val feedPostsRepo by lazy { FirebaseFeedPostsRepository() }
    val notificationsRepo by lazy { FirebaseNotificationsRepository() }
    val authManager by lazy { FirebaseAuthManager() }
    val searchRepo by lazy { FirebaseSearchRepository() }

    override fun onCreate() {
        super.onCreate()
        NotificationsCreator(notificationsRepo, usersRepo, feedPostsRepo)
        SearchPostsCreator(searchRepo)
    }
}
package com.alexbezhan.instagram.data.firebase

import com.alexbezhan.instagram.data.*

private val authRepo = FirebaseAuthRepository()
private val commentsRepo = FirebaseCommentsRepository()
private val feedPostsRepo = FirebaseFeedPostsRepository()
private val imagesRepo = FirebaseImagesRepository()
private val likesRepo = FirebaseLikesRepository()
private val notificationsRepo = FirebaseNotificationsRepository()
private val usersRepo = FirebaseUsersRepository()

object FirebaseRepository :
        AuthRepository by authRepo,
        CommentsRepository by commentsRepo,
        FeedPostsRepository by feedPostsRepo,
        ImagesRepository by imagesRepo,
        LikesRepository by likesRepo,
        NotificationsRepository by notificationsRepo,
        UsersRepository by usersRepo, Repository
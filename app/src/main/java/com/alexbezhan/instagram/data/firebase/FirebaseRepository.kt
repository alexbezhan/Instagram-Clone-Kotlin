package com.alexbezhan.instagram.data.firebase

import com.alexbezhan.instagram.data.*

private val auth = FirebaseAuthRepository()
private val comments = FirebaseCommentsRepository()
private val feedPosts = FirebaseFeedPostsRepository()
private val images = FirebaseImagesRepository()
private val likes = FirebaseLikesRepository()
private val notifications = FirebaseNotificationsRepository()
private val users = FirebaseUsersRepository()

object FirebaseRepository :
        AuthRepository by auth,
        CommentsRepository by comments,
        FeedPostsRepository by feedPosts,
        ImagesRepository by images,
        LikesRepository by likes,
        NotificationsRepository by notifications,
        UsersRepository by users, Repository
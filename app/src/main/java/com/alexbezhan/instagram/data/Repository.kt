package com.alexbezhan.instagram.data

interface Repository : AuthRepository, CommentsRepository, FeedPostsRepository, ImagesRepository,
    LikesRepository, NotificationsRepository, UsersRepository
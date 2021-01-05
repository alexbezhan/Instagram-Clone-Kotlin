package com.alexbezhan.instagram.screens.comments

import androidx.lifecycle.LiveData
import com.alexbezhan.instagram.data.FeedPostsRepository
import com.alexbezhan.instagram.data.UsersRepository
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class CommentsViewModel(private val feedPostsRepo: FeedPostsRepository,
                        usersRepo: UsersRepository,
                        onFailureListener: OnFailureListener) :
        BaseViewModel(onFailureListener) {
    lateinit var comments: LiveData<List<Comment>>
    private lateinit var postId: String
    val user: LiveData<User> = usersRepo.getUser()

    fun init(postId: String) {
        this.postId = postId
        comments = feedPostsRepo.getComments(postId)
    }

    fun createComment(text: String, user: User) {
        val comment = Comment(
                uid = user.uid,
                username = user.username,
                photo = user.photo,
                text = text)
        feedPostsRepo.createComment(postId, comment).addOnFailureListener(onFailureListener)
    }
}
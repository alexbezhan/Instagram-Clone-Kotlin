package com.alexbezhan.instagram.activities.home.comments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.livedata.*

private val errorComp = ErrorLiveDataComponent()

class CommentsViewModel : ViewModel(), HasErrorLiveData by errorComp,
        HasUserLiveData by UserLiveDataComponent() {
    private lateinit var postId: String

    lateinit var comments: LiveData<List<Comment>>
        private set

    fun init(postId: String) {
        this.postId = postId
        comments = Transformations.map(FirebaseLiveData(FirebaseHelper.database.child("comments").child(postId)),
                {
                    it.children.map { it.getValue(Comment::class.java)!! }
                })
    }

    fun postComment(comment: String, user: User) {
        if (comment.isNotEmpty()) {
            val commentObj = Comment(uid = user.uid, photo = user.photo, username = user.username,
                    text = comment)
            FirebaseHelper.database.child("comments").child(postId).push().setValue(commentObj)
                    .addOnFailureListener(errorComp.onFailureListener)
        }
    }
}
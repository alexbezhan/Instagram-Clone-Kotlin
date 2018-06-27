package com.alexbezhan.instagram.data

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.models.Comment
import com.google.android.gms.tasks.Task

interface CommentsRepository {
    fun getComments(postId: String): LiveData<List<Comment>>
    fun createComment(postId: String, comment: Comment): Task<String>
    fun commentsCount(postId: String): LiveData<Int>
}
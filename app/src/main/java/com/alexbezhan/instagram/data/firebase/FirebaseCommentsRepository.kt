package com.alexbezhan.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.CommentsRepository
import com.alexbezhan.instagram.data.firebase.utils.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.data.firebase.utils.database
import com.alexbezhan.instagram.data.live.FirebaseLiveData
import com.alexbezhan.instagram.data.live.map
import com.alexbezhan.instagram.data.task
import com.alexbezhan.instagram.models.Comment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class FirebaseCommentsRepository : CommentsRepository {
    override fun commentsCount(postId: String): LiveData<Int> =
            FirebaseLiveData { "comments/$postId" }.map {
                it.children.count()
            }

    override fun getComments(postId: String): LiveData<List<Comment>> =
            FirebaseLiveData { "comments/$postId" }.map {
                it.children.map { it.getValue(Comment::class.java)!! }
            }

    override fun createComment(postId: String, comment: Comment): Task<String> =
            task { taskSource ->
                val commentRef = database.child("comments").child(postId).push()
                commentRef.setValue(comment)
                        .onSuccessTask { Tasks.forResult(commentRef.key) }
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
            }

}
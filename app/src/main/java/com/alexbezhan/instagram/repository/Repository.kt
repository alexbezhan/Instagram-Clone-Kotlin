package com.alexbezhan.instagram.repository

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.activities.map
import com.alexbezhan.instagram.activities.task
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.firebase.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

interface Repository {
    fun signIn(email: String, password: String): Task<Unit>
    fun getFeedPosts(uid: String): LiveData<List<FeedPost>>
    fun getFeedPost(uid: String, postId: String): LiveData<FeedPost>
    fun getComments(postId: String): LiveData<List<Comment>>
    fun getUser(uid: String): LiveData<User>
    fun createComment(postId: String, comment: Comment): Task<String>
    fun setNotificationsRead(uid: String, notificationsIds: List<String>, read: Boolean): Task<Void>
    fun getImages(uid: String): LiveData<List<String>>
}

class FirebaseRepository : Repository {
    override fun getFeedPosts(uid: String): LiveData<List<FeedPost>> =
            FirebaseLiveData(database.child("feed-posts").child(uid)).map {
                it.children.map { it.asFeedPost()!! }
            }

    override fun getComments(postId: String): LiveData<List<Comment>> =
            FirebaseLiveData(FirebaseHelper.database.child("comments").child(postId)).map {
                it.children.map { it.getValue(Comment::class.java)!! }
            }

    override fun getFeedPost(uid: String, postId: String): LiveData<FeedPost> =
            FirebaseLiveData(FirebaseHelper.database.child("feed-posts").child(uid)
                    .child(postId)).map {
                it.asFeedPost()!!
            }

    override fun getUser(uid: String): LiveData<User> =
            FirebaseLiveData(FirebaseHelper.database.child("users").child(uid)).map {
                it.asUser()!!
            }

    override fun createComment(postId: String, comment: Comment): Task<String> =
            task { taskSource ->
                val commentRef = FirebaseHelper.database.child("comments").child(postId).push()
                commentRef.setValue(comment)
                        .onSuccessTask { Tasks.forResult(commentRef.key) }
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
            }

    override fun signIn(email: String, password: String): Task<Unit> =
            task { taskSource ->
                FirebaseHelper.auth.signInWithEmailAndPassword(email, password)
                        .onSuccessTask { Tasks.forResult(Unit) }
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
            }

    override fun setNotificationsRead(uid: String, notificationsIds: List<String>,
                                      read: Boolean): Task<Void> {
        val updatesMap = notificationsIds.map { "/$it/read" to read }.toMap()
        return database.child("notifications").child(uid)
                .updateChildren(updatesMap)
    }

    override fun getImages(uid: String): LiveData<List<String>> =
            FirebaseLiveData(database.child("images").child(uid)).map {
                it.children.map { it.getValue(String::class.java)!! }
            }
}
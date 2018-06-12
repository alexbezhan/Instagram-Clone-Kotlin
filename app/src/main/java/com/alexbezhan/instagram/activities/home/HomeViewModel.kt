package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.activities.task
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.Notification
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.firebase.ValueEventListenerAdapter
import com.alexbezhan.instagram.utils.livedata.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot

private val errorComp = ErrorLiveDataComponent()

class HomeViewModel : ViewModel(), HasUserLiveData by UserLiveDataComponent(),
        HasErrorLiveData by errorComp {

    private var postLikes = mapOf<String, LiveData<FeedPostLikes>>()

    val feedPosts: LiveData<List<FeedPost>> = Transformations.map(
            FirebaseLiveData(database.child("feed-posts").child(FirebaseHelper.currentUid())),
            {
                it.children
                        .map { it.asFeedPost()!! }
                        .sortedByDescending { it.timestampDate() }
            })

    fun toggleLike(user: User, post: FeedPost) {
        val notificationsRef = database.child("notifications").child(post.uid)
        val likeRef = database.child("likes").child(post.id).child(user.uid)

        fun addNotification(): Pair<String, Task<Void>> {
            val notificationRef = notificationsRef.push()
            return (notificationRef.key to notificationRef.setValue(Notification(
                    id = notificationRef.key,
                    uid = user.uid,
                    photo = user.photo,
                    username = user.username,
                    type = NotificationType.LIKE,
                    postId = post.id,
                    postImage = post.image)))
        }

        fun removeNotification(id: String) =
                notificationsRef.child(id).removeValue()

        likeRef.addListenerForSingleValueEvent(ValueEventListenerAdapter { likeValue ->
            val task = if (likeValue.exists()) {
                Tasks.whenAll(removeNotification(likeValue.asNotificationId()), likeRef.removeValue())
            } else {
                val (notificationId, addNotificationTask) = addNotification()
                Tasks.whenAll(addNotificationTask, likeRef.setValue(notificationId))
            }
            task.addOnFailureListener(errorComp.onFailureListener)
        })
    }

    private fun DataSnapshot.asNotificationId() = getValue(String::class.java)!!

    fun observeLikes(postId: String, owner: LifecycleOwner, observer: Observer<FeedPostLikes>) {
        val createNewObserver = postLikes[postId] == null
        if (createNewObserver) {
            val data = Transformations.map(FirebaseLiveData(
                    database.child("likes").child(postId)), {
                val userLikes = it.children.map { it.key }.toSet()
                FeedPostLikes(
                        userLikes.size,
                        userLikes.contains(FirebaseHelper.currentUid()))
            })
            data.observe(owner, observer)
            postLikes += (postId to data)
        }
    }
}
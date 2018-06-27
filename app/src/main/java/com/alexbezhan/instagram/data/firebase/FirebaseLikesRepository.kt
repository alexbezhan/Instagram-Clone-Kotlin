package com.alexbezhan.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.LikesRepository
import com.alexbezhan.instagram.data.firebase.utils.asString
import com.alexbezhan.instagram.data.firebase.utils.database
import com.alexbezhan.instagram.data.firebase.utils.getRefValue
import com.alexbezhan.instagram.data.live.FirebaseLiveData
import com.alexbezhan.instagram.data.live.map
import com.alexbezhan.instagram.models.FeedPostLike
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class FirebaseLikesRepository : LikesRepository {
    override fun setLikeValue(postId: String, uid: String, notificationId: String): Task<Void> =
            getLikeRef(postId, uid).setValue(notificationId)

    override fun deleteLikeValue(postId: String, uid: String): Task<Void> =
            getLikeRef(postId, uid).removeValue()

    override fun getLikeValue(postId: String, uid: String): Task<String?> =
            getRefValue(getLikeRef(postId, uid))
                    .onSuccessTask { Tasks.forResult(it?.asString()) }

    override fun likes(postId: String): LiveData<List<FeedPostLike>> =
            FirebaseLiveData { "likes/$postId" }.map {
                it.children.map {
                    FeedPostLike(
                            userId = it.key,
                            notificationId = it.getValue(String::class.java)!!)
                }
            }

    private fun getLikeRef(postId: String, uid: String) =
            database.child("likes").child(postId).child(uid)

}
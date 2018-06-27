package com.alexbezhan.instagram.data

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.models.*
import com.google.android.gms.tasks.Task

interface LikesRepository {
    fun likes(postId: String): LiveData<List<FeedPostLike>>
    fun getLikeValue(postId: String, uid: String): Task<String?>
    fun setLikeValue(postId: String, uid: String, notificationId: String): Task<Void>
    fun deleteLikeValue(postId: String, uid: String): Task<Void>
}
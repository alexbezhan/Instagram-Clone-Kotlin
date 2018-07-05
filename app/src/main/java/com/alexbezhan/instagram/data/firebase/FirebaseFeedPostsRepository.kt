package com.alexbezhan.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.common.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.common.ValueEventListenerAdapter
import com.alexbezhan.instagram.common.task
import com.alexbezhan.instagram.common.toUnit
import com.alexbezhan.instagram.data.FeedPostLike
import com.alexbezhan.instagram.data.FeedPostsRepository
import com.alexbezhan.instagram.data.common.map
import com.alexbezhan.instagram.data.firebase.common.*
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.FeedPost
import com.google.android.gms.tasks.Task

class FirebaseFeedPostsRepository : FeedPostsRepository {
    override fun createComment(postId: String, comment: Comment): Task<Unit> =
            database.child("comments").child(postId).push().setValue(comment).toUnit()

    override fun getComments(postId: String): LiveData<List<Comment>> =
            FirebaseLiveData(database.child("comments").child(postId)).map {
                it.children.map { it.asComment()!! }
            }

    override fun getLikes(postId: String): LiveData<List<FeedPostLike>> =
            FirebaseLiveData(database.child("likes").child(postId)).map {
                it.children.map { FeedPostLike(it.key) }
            }

    override fun toggleLike(postId: String, uid: String): Task<Unit> {
        val reference = database.child("likes").child(postId).child(uid)
        return task { taskSource ->
            reference.addListenerForSingleValueEvent(ValueEventListenerAdapter {
                reference.setValueTrueOrRemove(!it.exists())
                taskSource.setResult(Unit)
            })
        }
    }

    override fun getFeedPosts(uid: String): LiveData<List<FeedPost>> =
            FirebaseLiveData(database.child("feed-posts").child(uid)).map {
                it.children.map { it.asFeedPost()!! }
            }

    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
            task { taskSource ->
                database.child("feed-posts").child(postsAuthorUid)
                        .orderByChild("uid")
                        .equalTo(postsAuthorUid)
                        .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                            val postsMap = it.children.map { it.key to it.value }.toMap()
                            database.child("feed-posts").child(uid).updateChildren(postsMap)
                                    .toUnit()
                                    .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                        })
            }

    override fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
            task { taskSource ->
                database.child("feed-posts").child(uid)
                        .orderByChild("uid")
                        .equalTo(postsAuthorUid)
                        .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                            val postsMap = it.children.map { it.key to null }.toMap()
                            database.child("feed-posts").child(uid).updateChildren(postsMap)
                                    .toUnit()
                                    .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                        })
            }

}
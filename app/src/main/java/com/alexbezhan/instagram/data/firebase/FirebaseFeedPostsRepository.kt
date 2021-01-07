package com.alexbezhan.instagram.data.firebase

import androidx.lifecycle.LiveData
import com.alexbezhan.instagram.common.*
import com.alexbezhan.instagram.data.FeedPostLike
import com.alexbezhan.instagram.data.FeedPostsRepository
import com.alexbezhan.instagram.data.common.map
import com.alexbezhan.instagram.data.firebase.common.FirebaseLiveData
import com.alexbezhan.instagram.data.firebase.common.database
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.FeedPost
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot

class FirebaseFeedPostsRepository : FeedPostsRepository {
    override fun createFeedPost(uid: String, feedPost: FeedPost): Task<Unit> {
        val reference = database.child("feed-posts").child(uid).push()
        return reference.setValue(feedPost).toUnit().addOnSuccessListener {
            EventBus.publish(Event.CreateFeedPost(feedPost.copy(id = reference.key!!)))
        }
    }

    override fun createComment(postId: String, comment: Comment): Task<Unit> =
            database.child("comments").child(postId).push().setValue(comment).toUnit()
                    .addOnSuccessListener {
                        EventBus.publish(Event.CreateComment(postId, comment))
                    }

    override fun getComments(postId: String): LiveData<List<Comment>> =
            FirebaseLiveData(database.child("comments").child(postId)).map {
                it.children.map { it.asComment()!! }
            }

    override fun getLikes(postId: String): LiveData<List<FeedPostLike>> =
            FirebaseLiveData(database.child("likes").child(postId)).map {
                it.children.map { FeedPostLike(it.key!!) }
            }

    override fun toggleLike(postId: String, uid: String): Task<Unit> {
        val reference = database.child("likes").child(postId).child(uid)
        return task { taskSource ->
            reference.addListenerForSingleValueEvent(ValueEventListenerAdapter { like ->
                if (!like.exists()) {
                    reference.setValue(true).addOnSuccessListener {
                        EventBus.publish(Event.CreateLike(postId, uid))
                    }
                } else {
                    reference.removeValue()
                }
                taskSource.setResult(Unit)
            })
        }
    }

    override fun getFeedPost(uid: String, postId: String): LiveData<FeedPost> =
            FirebaseLiveData(database.child("feed-posts").child(uid).child(postId)).map {
                it.asFeedPost()!!
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

    private fun DataSnapshot.asFeedPost(): FeedPost? =
            getValue(FeedPost::class.java)?.copy(id = key!!)

    private fun DataSnapshot.asComment(): Comment? =
            getValue(Comment::class.java)?.copy(id = key!!)

}
package com.alexbezhan.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.FeedPostsRepository
import com.alexbezhan.instagram.data.firebase.utils.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.data.firebase.utils.ValueEventListenerAdapter
import com.alexbezhan.instagram.data.firebase.utils.database
import com.alexbezhan.instagram.data.live.FirebaseLiveData
import com.alexbezhan.instagram.data.live.map
import com.alexbezhan.instagram.data.task
import com.alexbezhan.instagram.data.toUnit
import com.alexbezhan.instagram.models.FeedPost
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot

class FirebaseFeedPostsRepository : FeedPostsRepository {
    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Void> =
            task { taskSource ->
                database.child("feed-posts").child(postsAuthorUid)
                        .orderByChild("uid")
                        .equalTo(postsAuthorUid)
                        .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                            val postsMap = it.children.map { it.key to it.value }.toMap()
                            database.child("feed-posts")
                                    .child(uid).updateChildren(postsMap)
                                    .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                        })
            }

    override fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Void> =
            task { taskSource ->
                database.child("feed-posts").child(uid)
                        .orderByChild("uid")
                        .equalTo(postsAuthorUid)
                        .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                            val postsMap = it.children.map { it.key to null }.toMap()
                            database.child("feed-posts")
                                    .child(uid).updateChildren(postsMap)
                                    .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                        })
            }

    override fun getFeedPosts(): LiveData<List<FeedPost>> =
            FirebaseLiveData { "feed-posts/$it" }.map {
                it.children.map { it.asFeedPost()!! }
            }

    override fun getFeedPost(uid: String, postId: String): LiveData<FeedPost> =
            FirebaseLiveData { "feed-posts/$uid/$postId" }.map {
                it.asFeedPost()!!
            }

    override fun addFeedPost(uid: String, post: FeedPost): Task<Unit> =
            database.child("feed-posts/$uid").push().setValue(post).toUnit()

    override fun getCurrentUserFeedPost(postId: String): LiveData<FeedPost> =
            FirebaseLiveData { "feed-posts/$it/$postId" }.map {
                it.asFeedPost()!!
            }

}

fun DataSnapshot.asFeedPost(): FeedPost? =
        getValue(FeedPost::class.java)?.copy(id = key)
package com.alexbezhan.instagram.data.firebase

import com.alexbezhan.instagram.activities.task
import com.alexbezhan.instagram.data.FeedPostsRepository
import com.alexbezhan.instagram.utils.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.utils.ValueEventListenerAdapter
import com.alexbezhan.instagram.utils.database
import com.alexbezhan.instagram.utils.toUnit
import com.google.android.gms.tasks.Task

class FirebaseFeedPostsRepository : FeedPostsRepository {
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
package com.alexbezhan.instagram.activities.profile.friends

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.*
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.FirebaseHelper
import com.alexbezhan.instagram.utils.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.utils.ValueEventListenerAdapter
import com.google.android.gms.tasks.Tasks
import kotlinx.android.synthetic.main.activity_add_friends.*

class AddFriendsActivity : BaseActivity(), AddFriendsAdapter.Listener {
    private lateinit var mUser: User
    private lateinit var mUsers: List<User>
    private lateinit var mAdapter: AddFriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        mAdapter = AddFriendsAdapter(this)

        val uid = FirebaseHelper.currentUid()!!

        back_image.setOnClickListener { finish() }

        add_friends_recycler.adapter = mAdapter
        add_friends_recycler.layoutManager = LinearLayoutManager(this)

        FirebaseHelper.database.child("users").addValueEventListener(ValueEventListenerAdapter {
            val allUsers = it.children.map { it.asUser()!! }
            val (userList, otherUsersList) = allUsers.partition { it.uid == uid }
            mUser = userList.first()
            mUsers = otherUsersList

            mAdapter.update(mUsers, mUser.follows)
        })
    }

    override fun follow(uid: String) {
        setFollow(uid, true) {
            mAdapter.followed(uid)
        }
    }

    override fun unfollow(uid: String) {
        setFollow(uid, false) {
            mAdapter.unfollowed(uid)
        }
    }

    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {
        val followsTask = FirebaseHelper.database.child("users").child(mUser.uid).child("follows")
                .child(uid).setValueTrueOrRemove(follow)
        val followersTask = FirebaseHelper.database.child("users").child(uid).child("followers")
                .child(mUser.uid).setValueTrueOrRemove(follow)

        val feedPostsTask = task<Void> { taskSource ->
            FirebaseHelper.database.child("feed-posts").child(uid)
                    .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                        val postsMap = if (follow) {
                            it.children.map { it.key to it.value }.toMap()
                        } else {
                            it.children.map { it.key to null }.toMap()
                        }
                        FirebaseHelper.database.child("feed-posts").child(mUser.uid).updateChildren(postsMap)
                                .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                    })
        }

        Tasks.whenAll(followsTask, followersTask, feedPostsTask).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }
}
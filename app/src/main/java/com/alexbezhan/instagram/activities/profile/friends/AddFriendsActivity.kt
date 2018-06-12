package com.alexbezhan.instagram.activities.profile.friends

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.*
import kotlinx.android.synthetic.main.activity_add_friends.*

class AddFriendsActivity : BaseActivity(), AddFriendsAdapter.Listener {
    private lateinit var mAdapter: AddFriendsAdapter
    private lateinit var mModel: AddFriendsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        mAdapter = AddFriendsAdapter(this)

        back_image.setOnClickListener { finish() }

        add_friends_recycler.adapter = mAdapter
        add_friends_recycler.layoutManager = LinearLayoutManager(this)

        mModel = ViewModelProviders.of(this).get(AddFriendsViewModel::class.java)
        mModel.userAndFriends.observe(this, Observer {
            it?.let { (user, friends) ->
                mAdapter.update(friends, user.follows)
            }
        })
        mModel.followError.observe(this, Observer {
            it?.let { showToast(it) }
        })
    }

    override fun follow(uid: String) {
        mModel.follow(uid)
    }

    override fun unfollow(uid: String) {
        mModel.unfollow(uid)
    }
}
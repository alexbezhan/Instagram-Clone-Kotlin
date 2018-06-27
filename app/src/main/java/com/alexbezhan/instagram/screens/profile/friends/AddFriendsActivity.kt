package com.alexbezhan.instagram.screens.profile.friends

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.screens.common.BaseActivity
import com.alexbezhan.instagram.models.User
import kotlinx.android.synthetic.main.activity_add_friends.*

class AddFriendsActivity : BaseActivity(), AddFriendsAdapter.Listener {
    private lateinit var mAdapter: AddFriendsAdapter
    private lateinit var mModel: AddFriendsViewModel
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        mAdapter = AddFriendsAdapter(this)

        back_image.setOnClickListener { finish() }

        add_friends_recycler.adapter = mAdapter
        add_friends_recycler.layoutManager = LinearLayoutManager(this)

        mModel = initModel(AddFriendsViewModelFactory())
        mModel.userAndFriends.observe(this, Observer {
            it?.let { (user, friends) ->
                mUser = user
                mAdapter.update(friends, user.follows)
            }
        })
    }

    override fun toggleFollow(uid: String) {
        mModel.toggleFollow(mUser, uid)
    }
}
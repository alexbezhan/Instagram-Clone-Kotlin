package com.alexbezhan.instagram.activities.profile.friends

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.ShowToastErrorObserver
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

        mModel = ViewModelProviders.of(this).get(AddFriendsViewModel::class.java)
        mModel.userAndFriends.observe(this, Observer {
            it?.let { (user, friends) ->
                mUser = user
                mAdapter.update(friends, user.follows)
            }
        })
        mModel.error.observe(this, ShowToastErrorObserver(this))
    }

    override fun toggleFollow(uid: String) {
        mModel.toggleFollow(mUser, uid)
    }
}
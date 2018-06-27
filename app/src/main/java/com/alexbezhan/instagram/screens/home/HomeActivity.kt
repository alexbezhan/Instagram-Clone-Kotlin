package com.alexbezhan.instagram.screens.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.screens.common.views.BottomNavBar
import com.alexbezhan.instagram.screens.common.disableChangeAnimation
import com.alexbezhan.instagram.models.User
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseFeedActivity() {
    private val TAG = "HomeActivity"
    override lateinit var mAdapter: FeedAdapter
    override lateinit var mModel: HomeViewModel
    override lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation(BottomNavBar.POSITION_HOME)

        mAdapter = FeedAdapter(this)
        feed_recycler.disableChangeAnimation()
        feed_recycler.layoutManager = LinearLayoutManager(this)
        feed_recycler.adapter = mAdapter

        mModel = initModel(HomeViewModelFactory())
        mModel.user.observe(this, Observer { it?.let { mUser = it } })
        mModel.feedPosts.observe(this, Observer {
            it?.let { mAdapter.items = it }
        })
    }
}

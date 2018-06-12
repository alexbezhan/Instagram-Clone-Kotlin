package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.*
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.*
import com.alexbezhan.instagram.activities.home.comments.CommentsActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(0), FeedAdapter.Listener {
    private val TAG = "HomeActivity"
    private lateinit var mAdapter: FeedAdapter
    private lateinit var mModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

        mAdapter = FeedAdapter(this)
        feed_recycler.adapter = mAdapter
        feed_recycler.layoutManager = LinearLayoutManager(this)
        mModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        mModel.feedPosts.observe(this, Observer {
            it?.let { mAdapter.items = it }
        })
    }

    override fun toggleLike(postId: String) {
        mModel.toggleLike(postId)
    }

    override fun loadLikes(postId: String, position: Int) {
        mModel.observeLikes(postId, this, Observer {
            it?.let {
                mAdapter.updatePostLikes(position, it)
            }
        })
    }

    override fun comment(postId: String) {
        val intent = Intent(this, CommentsActivity::class.java)
        intent.putExtra(CommentsActivity.EXTRA_POST_ID, postId)
        startActivity(intent)
    }
}
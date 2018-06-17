package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.BottomNavBar
import com.alexbezhan.instagram.activities.disableChangeAnimation
import com.alexbezhan.instagram.activities.home.comments.CommentsActivity
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), FeedAdapter.Listener {
    private val TAG = "HomeActivity"
    private lateinit var mAdapter: FeedAdapter
    private lateinit var mModel: HomeViewModel
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAuthenticated()) {
            setContentView(R.layout.activity_home)
            Log.d(TAG, "onCreate")
            setupBottomNavigation(BottomNavBar.POSITION_HOME)

            mAdapter = FeedAdapter(this)
            feed_recycler.disableChangeAnimation()
            feed_recycler.adapter = mAdapter
            feed_recycler.layoutManager = LinearLayoutManager(this)

            mModel = initModel()
            mModel.user.observe(this, Observer { it?.let { mUser = it } })
            mModel.feedPosts.observe(this, Observer {
                it?.let { mAdapter.items = it }
            })
        }
    }

    override fun toggleLike(post: FeedPost) {
        mModel.toggleLike(mUser, post)
    }

    override fun loadStats(postId: String, position: Int) {
        mModel.observePostStats(postId, this, Observer {
            it?.let {
                mAdapter.updatePostStats(position, it)
            }
        })
    }

    override fun comment(postId: String, uid: String) {
        val intent = Intent(this, CommentsActivity::class.java)
        intent.putExtra(CommentsActivity.EXTRA_POST_ID, postId)
        intent.putExtra(CommentsActivity.EXTRA_POST_UID, uid)
        intent.putExtra(CommentsActivity.EXTRA_START_TYPING_COMMENT, true)
        startActivity(intent)
    }

    override fun showComments(postId: String, uid: String) {
        val intent = Intent(this, CommentsActivity::class.java)
        intent.putExtra(CommentsActivity.EXTRA_POST_ID, postId)
        intent.putExtra(CommentsActivity.EXTRA_POST_UID, uid)
        intent.putExtra(CommentsActivity.EXTRA_START_TYPING_COMMENT, false)
        startActivity(intent)
    }
}

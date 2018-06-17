package com.alexbezhan.instagram.activities.postdetails

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.disableChangeAnimation
import com.alexbezhan.instagram.activities.home.FeedAdapter
import com.alexbezhan.instagram.activities.home.comments.CommentsActivity
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import kotlinx.android.synthetic.main.activity_post_details.*

class PostDetailsActivity : BaseActivity(), FeedAdapter.Listener {
    private val TAG = "PostDetailsActivity"

    private lateinit var mModel: PostDetailsViewModel
    private lateinit var mUser: User
    private lateinit var mAdapter: FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAuthenticated()) {
            setContentView(R.layout.activity_post_details)

            mAdapter = FeedAdapter(this)
            post_recycler.disableChangeAnimation()
            post_recycler.layoutManager = LinearLayoutManager(this)
            post_recycler.adapter = mAdapter
            back_image.setOnClickListener { finish() }

            val postId = intent.getStringExtra(EXTRA_POST_ID)
            mModel = initModel()
            mModel.start(postId)
            mModel.user.observe(this, Observer { it?.let { mUser = it } })
            mModel.post.observe(this, Observer {
                it?.let { mAdapter.items = listOf(it) }
            })
        }
    }

    override fun toggleLike(post: FeedPost) =
            mModel.toggleLike(mUser, post)

    override fun loadStats(postId: String, position: Int) =
            mModel.observePostStats(postId, this, Observer {
                it?.let {
                    mAdapter.updatePostStats(position, it)
                }
            })

    override fun comment(postId: String, uid: String) {
        val intent = Intent(this, CommentsActivity::class.java)
        CommentsActivity.setupStartIntent(intent, postId = postId, postUid = uid,
                startTypingComment = true)
        startActivity(intent)
    }

    override fun showComments(postId: String, uid: String) {
        val intent = Intent(this, CommentsActivity::class.java)
        CommentsActivity.setupStartIntent(intent, postId = postId, postUid = uid,
                startTypingComment = false)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_POST_ID = "post_id"

        fun setupStartIntent(intent: Intent, postId: String) {
            intent.putExtra(PostDetailsActivity.EXTRA_POST_ID, postId)
        }
    }
}

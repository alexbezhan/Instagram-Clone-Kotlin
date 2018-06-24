package com.alexbezhan.instagram.activities.postdetails

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.disableChangeAnimation
import com.alexbezhan.instagram.activities.home.BaseFeedActivity
import com.alexbezhan.instagram.activities.home.FeedAdapter
import com.alexbezhan.instagram.models.User
import kotlinx.android.synthetic.main.activity_post_details.*

class PostDetailsActivity : BaseFeedActivity() {
    private val TAG = "PostDetailsActivity"
    override lateinit var mModel: PostDetailsViewModel
    override lateinit var mUser: User
    override lateinit var mAdapter: FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        mAdapter = FeedAdapter(this)
        post_recycler.disableChangeAnimation()
        post_recycler.layoutManager = LinearLayoutManager(this)
        post_recycler.adapter = mAdapter
        back_image.setOnClickListener { finish() }

        val postId = intent.getStringExtra(EXTRA_POST_ID)
        mModel = initModel(PostDetailsViewModelFactory())
        mModel.start(postId)
        mModel.user.observe(this, Observer {
            it?.let { mUser = it }
        })
        mModel.post.observe(this, Observer {
            it?.let { mAdapter.items = listOf(it) }
        })
    }

    companion object {
        private const val EXTRA_POST_ID = "post_id"

        fun start(context: Context, postId: String) {
            val intent = Intent(context, PostDetailsActivity::class.java).apply {
                putExtra(PostDetailsActivity.EXTRA_POST_ID, postId)
            }
            context.startActivity(intent)
        }
    }
}

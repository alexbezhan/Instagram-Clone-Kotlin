package com.alexbezhan.instagram.activities.home.comments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.loadUserPhoto
import com.alexbezhan.instagram.models.User
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : BaseActivity() {
    companion object {
        const val EXTRA_POST_ID = "post_id"
    }

    private lateinit var mModel: CommentsViewModel
    private lateinit var mUser: User
    private lateinit var mAdapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        post_text.setOnClickListener {
            mModel.postComment(comment_input.text.toString(), mUser)
            comment_input.setText("")
        }

        mAdapter = CommentsAdapter()
        comments_recycler.layoutManager = LinearLayoutManager(this)
        comments_recycler.adapter = mAdapter
        back_image.setOnClickListener { finish() }

        mModel = initModel()
        mModel.start(intent.getStringExtra(EXTRA_POST_ID))
        mModel.user.observe(this, Observer {
            it?.let {
                mUser = it
                user_photo.loadUserPhoto(mUser.photo)
            }
        })
        mModel.comments.observe(this, Observer {
            it?.let {
                mAdapter.items = it
            }
        })
    }

    override fun onStart() {
        super.onStart()
        comment_input.requestFocus()
    }
}
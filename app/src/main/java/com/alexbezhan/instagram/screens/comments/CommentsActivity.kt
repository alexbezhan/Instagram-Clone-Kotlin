package com.alexbezhan.instagram.screens.comments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.screens.common.BaseActivity
import com.alexbezhan.instagram.screens.common.loadUserPhoto
import com.alexbezhan.instagram.screens.common.setupAuthGuard
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : BaseActivity() {
    private lateinit var mAdapter: CommentsAdapter
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val postId = intent.getStringExtra(EXTRA_POST_ID) ?: return finish()

        back_image.setOnClickListener { finish() }

        setupAuthGuard {
            mAdapter = CommentsAdapter()
            comments_recycler.layoutManager = LinearLayoutManager(this)
            comments_recycler.adapter = mAdapter

            val viewModel = initViewModel<CommentsViewModel>()
            viewModel.init(postId)
            viewModel.user.observe(this, Observer {
                it?.let {
                    mUser = it
                    user_photo.loadUserPhoto(mUser.photo)
                }
            })
            viewModel.comments.observe(this, Observer {
                it?.let {
                    mAdapter.updateComments(it)
                }
            })
            post_comment_text.setOnClickListener {
                viewModel.createComment(comment_text.text.toString(), mUser)
                comment_text.setText("")
            }
        }
    }

    companion object {
        private const val EXTRA_POST_ID = "POST_ID"

        fun start(context: Context, postId: String) {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra(EXTRA_POST_ID, postId)
            context.startActivity(intent)
        }
    }
}
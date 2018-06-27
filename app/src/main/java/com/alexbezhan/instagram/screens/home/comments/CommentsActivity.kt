package com.alexbezhan.instagram.screens.home.comments

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.WindowManager
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.screens.common.BaseActivity
import com.alexbezhan.instagram.screens.common.hideSoftKeyboard
import com.alexbezhan.instagram.screens.common.loadUserPhoto
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : BaseActivity() {
    private lateinit var mModel: CommentsViewModel
    private lateinit var mUser: User
    private lateinit var mAdapter: CommentsAdapter
    private lateinit var mPostAuthor: User
    private lateinit var mPost: FeedPost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        mModel = initModel(CommentsViewModelFactory())
        mModel.start(
                postId = intent.getStringExtra(EXTRA_POST_ID),
                postUid = intent.getStringExtra(EXTRA_POST_UID))
        mModel.user.observe(this, Observer {
            it?.let {
                mUser = it
                user_photo.loadUserPhoto(mUser.photo)
            }
        })
        mModel.post.observe(this, Observer {
            it?.let {
                mPost = it
            }
        })
        mModel.postAuthor.observe(this, Observer {
            it?.let {
                mPostAuthor = it
            }
        })
        mModel.comments.observe(this, Observer {
            it?.let {
                mAdapter.items = it
            }
        })

        post_text.setOnClickListener {
            mModel.postComment(comment_input.text.toString(), mUser, mPostAuthor, mPost)
            comment_input.setText("")
        }

        mAdapter = CommentsAdapter()
        comments_recycler.layoutManager = LinearLayoutManager(this)
        comments_recycler.adapter = mAdapter
        back_image.setOnClickListener {
            it.hideSoftKeyboard()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (intent.getBooleanExtra(EXTRA_START_TYPING_COMMENT, false)) {
            comment_input.requestFocus()
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        }
    }

    companion object {
        private const val EXTRA_POST_ID = "post_id"
        private const val EXTRA_POST_UID = "post_uid"
        private const val EXTRA_START_TYPING_COMMENT = "start_typing_comment"

        fun start(context: Context, postId: String, postUid: String, startTypingComment: Boolean) {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra(CommentsActivity.EXTRA_POST_ID, postId)
            intent.putExtra(CommentsActivity.EXTRA_POST_UID, postUid)
            intent.putExtra(CommentsActivity.EXTRA_START_TYPING_COMMENT, startTypingComment)
            context.startActivity(intent)
        }
    }
}
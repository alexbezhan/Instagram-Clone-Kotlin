package com.alexbezhan.instagram.activities.home

import android.arch.lifecycle.Observer
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.home.comments.CommentsActivity
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User

abstract class BaseFeedActivity : BaseActivity(), FeedAdapter.Listener {
    protected abstract val mModel: BaseFeedViewModel
    protected abstract val mUser: User
    protected abstract val mAdapter: FeedAdapter

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
        CommentsActivity.start(this, postId = postId, postUid = uid,
                startTypingComment = true)
    }

    override fun showComments(postId: String, uid: String) {
        CommentsActivity.start(this, postId = postId, postUid = uid,
                startTypingComment = false)
    }
}
package com.alexbezhan.instagram.activities.postdetails

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.activities.home.DefaultFeedPostListener
import com.alexbezhan.instagram.activities.home.FeedAdapter
import com.alexbezhan.instagram.activities.home.FeedPostLikes
import com.alexbezhan.instagram.activities.home.FeedPostListener
import com.alexbezhan.instagram.activities.home.comments.CommentsActivity
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData
import kotlinx.android.synthetic.main.activity_post_details.*

class PostDetailsViewModel : BaseViewModel(), FeedPostListener {
    private val feedPostListener = DefaultFeedPostListener(onFailureListener)

    private lateinit var postId: String
    lateinit var post: LiveData<FeedPost>

    fun start(postId: String) {
        this.postId = postId
        post = Transformations.map(
                FirebaseLiveData(database.child("feed-posts").child(currentUid()!!).child(postId)),
                {
                    it.asFeedPost()!!
                })
    }

    override fun observeLikes(postId: String, owner: LifecycleOwner,
                              observer: Observer<FeedPostLikes>) =
            feedPostListener.observeLikes(postId, owner, observer)

    override fun toggleLike(currentUser: User, post: FeedPost) =
            feedPostListener.toggleLike(currentUser, post)

}

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

    override fun loadLikes(postId: String, position: Int) =
            mModel.observeLikes(postId, this, Observer {
                it?.let {
                    mAdapter.updatePostLikes(position, it)
                }
            })

    override fun comment(postId: String) {
        val intent = Intent(this, CommentsActivity::class.java)
        intent.putExtra(CommentsActivity.EXTRA_POST_ID, postId)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_POST_ID = "post_id"
    }
}

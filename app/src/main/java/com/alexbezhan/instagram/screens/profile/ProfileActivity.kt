package com.alexbezhan.instagram.screens.profile

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.screens.common.BaseActivity
import com.alexbezhan.instagram.screens.common.views.BottomNavBar
import com.alexbezhan.instagram.screens.common.loadUserPhoto
import com.alexbezhan.instagram.screens.profile.edit.EditProfileActivity
import com.alexbezhan.instagram.screens.profile.friends.AddFriendsActivity
import com.alexbezhan.instagram.screens.profile.settings.ProfileSettingsActivity
import com.alexbezhan.instagram.models.User
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {
    private val TAG = "ProfileActivity"
    private lateinit var mAdapter: ProfileImagesAdapter
    private lateinit var mUser: User
    private var mAnotherUser: User? = null
    private lateinit var mModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation(BottomNavBar.POSITION_PROFILE)
        Log.d(TAG, "onCreate")

        mAdapter = ProfileImagesAdapter()
        images_recycler.layoutManager = GridLayoutManager(this, 3)
        images_recycler.adapter = mAdapter

        mModel = initModel(ProfileViewModelFactory(intent.extras?.getString(EXTRA_UID)))
        mModel.images.observe(this, Observer {
            it?.let { images ->
                mAdapter.items = images
                posts_count_text.text = images.size.toString()
            }
        })
        mModel.user.observe(this, Observer {
            it?.let {
                mUser = it
                bindOnUserChange()
            }
        })
        mModel.anotherUser?.observe(this, Observer {
            it?.let {
                mAnotherUser = it
                bindOnUserChange()
            }
        })

        mModel.openEditProfileUiCmd.observe(this, Observer {
            startActivity(Intent(this, EditProfileActivity::class.java))
        })
        mModel.openProfileSettingsUiCmd.observe(this, Observer {
            startActivity(Intent(this, ProfileSettingsActivity::class.java))
        })
        mModel.openAddFriendsUiCmd.observe(this, Observer {
            startActivity(Intent(this, AddFriendsActivity::class.java))
        })

        edit_profile_btn.setOnClickListener { mModel.onEditProfileClick() }
        settings_image.setOnClickListener { mModel.onSettingsClick() }
        add_friends_image.setOnClickListener { mModel.onAddFriendsClick() }
        follow_profile_btn.setOnClickListener { mModel.onToggleFollowClick(mUser) }

        if (mModel.isAnotherUser()) {
            add_friends_image.visibility = View.GONE
            settings_image.visibility = View.GONE
            edit_profile_btn.visibility = View.GONE
            follow_profile_btn.visibility = View.VISIBLE
        }
    }

    private fun bindOnUserChange() {
        bindFollowBtn()
        bindStats()
    }

    private fun bindFollowBtn() {
        if (mModel.isAnotherUser()) {
            mAnotherUser?.let { anotherUser ->
                val isFollowing = mUser.follows.containsKey(anotherUser.uid)
                follow_profile_btn.text =
                        if (isFollowing) getString(R.string.unfollow)
                        else getString(R.string.follow)
            }
        }
    }

    private fun bindStats() {
        val user = if (mModel.isAnotherUser()) mAnotherUser else mUser
        user?.let {
            profile_image.loadUserPhoto(user.photo)
            username_text.text = user.username
            followers_count_text.text = user.followers.size.toString()
            following_count_text.text = user.follows.size.toString()
        }
    }

    companion object {
        private const val EXTRA_UID = "uid"

        fun start(context: Context, uid: String) {
            val intent = Intent(context, ProfileActivity::class.java).apply {
                putExtra(ProfileActivity.EXTRA_UID, uid)
            }
            context.startActivity(intent)
        }
    }
}
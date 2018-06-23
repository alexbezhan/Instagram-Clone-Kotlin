package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.BottomNavBar
import com.alexbezhan.instagram.activities.loadUserPhoto
import com.alexbezhan.instagram.activities.profile.edit.EditProfileActivity
import com.alexbezhan.instagram.activities.profile.friends.AddFriendsActivity
import com.alexbezhan.instagram.activities.profile.settings.ProfileSettingsActivity
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.currentUid
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {
    private val TAG = "ProfileActivity"
    private lateinit var mAdapter: ProfileImagesAdapter
    private lateinit var mUid: String
    private lateinit var mUser: User
    private var mAnotherUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAuthenticated()) {
            setContentView(R.layout.activity_profile)
            setupBottomNavigation(BottomNavBar.POSITION_PROFILE)
            Log.d(TAG, "onCreate")

            mUid = intent.extras?.getString(EXTRA_UID) ?: currentUid()!!

            mAdapter = ProfileImagesAdapter()
            images_recycler.layoutManager = GridLayoutManager(this, 3)
            images_recycler.adapter = mAdapter

            val model = initModel<ProfileViewModel>()
            if (isAnotherUser()) {
                model.setAnotherUid(mUid)
            }
            model.images.observe(this, Observer {
                it?.let { images ->
                    mAdapter.items = images
                    posts_count_text.text = images.size.toString()
                }
            })
            model.user.observe(this, Observer {
                it?.let {
                    mUser = it
                    bindOnUserChange()
                }
            })
            model.anotherUser?.observe(this, Observer {
                it?.let {
                    mAnotherUser = it
                    bindOnUserChange()
                }
            })

            edit_profile_btn.setOnClickListener {
                startActivity(Intent(this, EditProfileActivity::class.java))
            }
            settings_image.setOnClickListener {
                startActivity(Intent(this, ProfileSettingsActivity::class.java))
            }
            add_friends_image.setOnClickListener {
                startActivity(Intent(this, AddFriendsActivity::class.java))
            }
            follow_profile_btn.setOnClickListener {
                model.toggleFollow(mUser, mUid)
            }

            if (isAnotherUser()) {
                add_friends_image.visibility = View.GONE
                settings_image.visibility = View.GONE
                edit_profile_btn.visibility = View.GONE
                follow_profile_btn.visibility = View.VISIBLE
            }
        }
    }

    private fun bindOnUserChange() {
        bindFollowBtn()
        bindStats()
    }

    private fun bindFollowBtn() {
        if (isAnotherUser()) {
            mAnotherUser?.let { anotherUser ->
                val isFollowing = mUser.follows.containsKey(anotherUser.uid)
                follow_profile_btn.text =
                        if (isFollowing) getString(R.string.unfollow)
                        else getString(R.string.follow)
            }
        }
    }

    private fun bindStats() {
        val user = if (isAnotherUser()) mAnotherUser else mUser
        user?.let {
            profile_image.loadUserPhoto(user.photo)
            username_text.text = user.username
            followers_count_text.text = user.followers.size.toString()
            following_count_text.text = user.follows.size.toString()
        }
    }

    private fun isAnotherUser() = mUid != currentUid()!!

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
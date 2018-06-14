package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.Observer
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
    private lateinit var mUser: User
    private lateinit var mAdapter: ProfileImagesAdapter
    private lateinit var mUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation(BottomNavBar.POSITION_PROFILE)
        Log.d(TAG, "onCreate")

        mUid = intent.extras?.getString(EXTRA_UID) ?: currentUid()!!

        edit_profile_btn.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        settings_image.setOnClickListener {
            startActivity(Intent(this, ProfileSettingsActivity::class.java))
        }
        add_friends_image.setOnClickListener {
            startActivity(Intent(this, AddFriendsActivity::class.java))
        }

        mAdapter = ProfileImagesAdapter()
        images_recycler.layoutManager = GridLayoutManager(this, 3)
        images_recycler.adapter = mAdapter

        val model = initModel<ProfileViewModel>()
        model.images.observe(this, Observer {
            it?.let { images ->
                mAdapter.items = images
                posts_count_text.text = images.size.toString()
            }
        })
        model.user.observe(this, Observer {
            it?.let {
                mUser = it
                profile_image.loadUserPhoto(mUser.photo)
                username_text.text = mUser.username
                followers_count_text.text = mUser.followers.size.toString()
                following_count_text.text = mUser.follows.size.toString()
            }
        })

        val isAnotherUser = mUid != currentUid()!!
        val privateControlsVisibility = if (isAnotherUser) View.GONE else View.VISIBLE
        add_friends_image.visibility = privateControlsVisibility
        settings_image.visibility = privateControlsVisibility
    }

    companion object {
        const val EXTRA_UID = "uid"
    }
}
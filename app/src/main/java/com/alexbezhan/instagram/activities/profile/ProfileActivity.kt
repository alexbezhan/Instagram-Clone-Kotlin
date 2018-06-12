package com.alexbezhan.instagram.activities.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.*
import com.alexbezhan.instagram.activities.profile.edit.EditProfileActivity
import com.alexbezhan.instagram.activities.profile.friends.AddFriendsActivity
import com.alexbezhan.instagram.activities.profile.settings.ProfileSettingsActivity
import com.alexbezhan.instagram.models.User
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(4) {
    private val TAG = "ProfileActivity"
    private lateinit var mUser: User
    private lateinit var mAdapter: ProfileImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()
        Log.d(TAG, "onCreate")

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

        val model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        model.images.observe(this, Observer {
            it?.let { mAdapter.items = it }
        })
        model.user.observe(this, Observer {
            it?.let {
                mUser = it
                profile_image.loadUserPhoto(mUser.photo)
                username_text.text = mUser.username
            }
        })
    }
}
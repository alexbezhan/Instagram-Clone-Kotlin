package com.alexbezhan.instagram.activities.profile

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
import com.alexbezhan.instagram.utils.FirebaseHelper
import com.alexbezhan.instagram.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(4) {
    private val TAG = "ProfileActivity"
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()
        Log.d(TAG, "onCreate")

        edit_profile_btn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        settings_image.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }
        add_friends_image.setOnClickListener{
            val intent = Intent(this, AddFriendsActivity::class.java)
            startActivity(intent)
        }

        mFirebase = FirebaseHelper(this)
        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.asUser()!!
            profile_image.loadUserPhoto(mUser.photo)
            username_text.text = mUser.username
        })

        images_recycler.layoutManager = GridLayoutManager(this, 3)
        mFirebase.database.child("images").child(mFirebase.currentUid()!!)
                .addValueEventListener(ValueEventListenerAdapter {
                    val images = it.children.map { it.getValue(String::class.java)!! }
                    images_recycler.adapter = ProfileImagesAdapter(images + images + images + images)
                })
    }
}
package com.alexbezhan.instagram.activities.share

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.profile.ProfileActivity
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.CameraHelper
import com.alexbezhan.instagram.utils.GlideApp
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity() {
    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraHelper
    private lateinit var mUser: User
    private lateinit var mModel: ShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAuthenticated()) {
            setContentView(R.layout.activity_share)
            Log.d(TAG, "onCreate")

            mCamera = CameraHelper(this)
            mCamera.takeCameraPicture()

            back_image.setOnClickListener { finish() }
            share_text.setOnClickListener { share() }

            mModel = initModel()
            mModel.user.observe(this, Observer { it?.let { mUser = it } })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                GlideApp.with(this).load(mCamera.imageUri).centerCrop().into(post_image)
            } else {
                finish()
            }
        }
    }

    private fun share() {
        val imageUri = mCamera.imageUri
        val caption = caption_input.text.toString()
        if (imageUri != null && caption.isNotEmpty()) {
            mModel.share(imageUri, caption, mUser).addOnSuccessListener {
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
        }
    }
}
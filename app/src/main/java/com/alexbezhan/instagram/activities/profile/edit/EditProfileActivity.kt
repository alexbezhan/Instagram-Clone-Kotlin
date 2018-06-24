package com.alexbezhan.instagram.activities.profile.edit

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.*
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.CameraHelper
import com.alexbezhan.instagram.views.PasswordDialog
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : BaseActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mCamera: CameraHelper
    private lateinit var mModel: EditProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        mCamera = CameraHelper(this)

        back_image.setOnClickListener {
            it.hideSoftKeyboard()
            finish()
        }
        save_image.setOnClickListener {
            mModel.onSaveProfileClick(newUser = readInputs(), currentUser = mUser)
        }
        change_photo_text.setOnClickListener { mCamera.takeCameraPicture() }

        mModel = initModel(EditProfileViewModelFactory())
        mModel.openPasswordConfirmDialogCmd.observe(this, Observer {
            PasswordDialog().show(supportFragmentManager, "password_dialog")
        })
        mModel.user.observe(this, Observer {
            it?.let {
                mUser = it
                bindUser()
            }
        })
        mModel.profileSavedEvent.observe(this, Observer {
            showToast(getString(R.string.profile_saved))
            back_image.hideSoftKeyboard()
            finish()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE && resultCode == RESULT_OK) {
            mModel.onImageTaken(mCamera.imageUri!!)
        }
    }

    private fun bindUser() {
        with(mUser) {
            name_input.setText(name)
            username_input.setText(username)
            website_input.setText(website)
            bio_input.setText(bio)
            email_input.setText(email)
            phone_input.setText(phone?.toString())
            profile_image.loadUserPhoto(photo)
        }
    }

    private fun readInputs(): User =
            User(
                    name = name_input.text.toString(),
                    username = username_input.text.toString(),
                    email = email_input.text.toString(),
                    website = website_input.text.toStringOrNull(),
                    bio = bio_input.text.toStringOrNull(),
                    phone = phone_input.text.toString().toLongOrNull()
            )

    override fun onPasswordConfirm(password: String) =
            mModel.onPasswordConfirm(currentUser = mUser, password = password)
}
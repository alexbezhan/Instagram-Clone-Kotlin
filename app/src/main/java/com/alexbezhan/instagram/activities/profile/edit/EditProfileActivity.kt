package com.alexbezhan.instagram.activities.profile.edit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.*
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.CameraHelper
import com.alexbezhan.instagram.utils.FirebaseHelper
import com.alexbezhan.instagram.utils.ValueEventListenerAdapter
import com.alexbezhan.instagram.views.PasswordDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : BaseActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mCamera: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        mCamera = CameraHelper(this)

        back_image.setOnClickListener { finish() }
        save_image.setOnClickListener { updateProfile() }
        change_photo_text.setOnClickListener { mCamera.takeCameraPicture() }

        FirebaseHelper.currentUserReference()
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    mUser = it.asUser()!!
                    name_input.setText(mUser.name)
                    username_input.setText(mUser.username)
                    website_input.setText(mUser.website)
                    bio_input.setText(mUser.bio)
                    email_input.setText(mUser.email)
                    phone_input.setText(mUser.phone?.toString())
                    profile_image.loadUserPhoto(mUser.photo)
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE && resultCode == RESULT_OK) {
            uploadUserPhoto(mCamera.imageUri!!) {
                val photoUrl = it.downloadUrl.toString()
                updateUserPhoto(photoUrl) {
                    mUser = mUser.copy(photo = photoUrl)
                    profile_image.loadUserPhoto(mUser.photo)
                }
            }
        }
    }

    private fun updateProfile() {
        mPendingUser = readInputs()
        val error = validate(mPendingUser)
        if (error == null) {
            if (mPendingUser.email == mUser.email) {
                updateUser(mPendingUser)
            } else {
                PasswordDialog().show(supportFragmentManager, "password_dialog")
            }
        } else {
            showToast(error)
        }
    }

    private fun readInputs(): User {
        return User(
                name = name_input.text.toString(),
                username = username_input.text.toString(),
                email = email_input.text.toString(),
                website = website_input.text.toStringOrNull(),
                bio = bio_input.text.toStringOrNull(),
                phone = phone_input.text.toString().toLongOrNull()
        )
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            reauthenticate(credential) {
                updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }
            }
        } else {
            showToast("You should enter your password")
        }
    }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any?>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone

        updateUser(updatesMap) {
            showToast("Profile saved")
            finish()
        }
    }

    private fun validate(user: User): String? =
            when {
                user.name.isEmpty() -> "Please enter name"
                user.username.isEmpty() -> "Please enter username"
                user.email.isEmpty() -> "Please enter email"
                else -> null
            }

    private fun uploadUserPhoto(photo: Uri, onSuccess: (UploadTask.TaskSnapshot) -> Unit) {
        FirebaseHelper.storage.child("users/${FirebaseHelper.currentUid()!!}/photo").putFile(photo)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess(it.result)
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
    }

    private fun updateUserPhoto(photoUrl: String, onSuccess: () -> Unit) {
        FirebaseHelper.database.child("users/${FirebaseHelper.currentUid()!!}/photo").setValue(photoUrl)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess()
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
    }

    private fun updateUser(updates: Map<String, Any?>, onSuccess: () -> Unit) {
        FirebaseHelper.database.child("users").child(FirebaseHelper.currentUid()!!).updateChildren(updates)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess()
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
    }

    private fun updateEmail(email: String, onSuccess: () -> Unit) {
        FirebaseHelper.auth.currentUser!!.updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        FirebaseHelper.auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }
}
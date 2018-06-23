package com.alexbezhan.instagram.activities.profile.edit

import android.net.Uri
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.google.firebase.auth.AuthCredential
import com.google.firebase.storage.UploadTask

class EditProfileViewModel : BaseViewModel() {

    private val uid = FirebaseHelper.currentUid()!!

    fun uploadUserPhoto(photo: Uri, onSuccess: (UploadTask.TaskSnapshot) -> Unit) {
        FirebaseHelper.storage.child("users/$uid/photo").putFile(photo)
                .addOnFailureListener(setErrorOnFailureListener)
                .addOnSuccessListener(onSuccess)
    }

    fun updateUserPhoto(photoUrl: String, onSuccess: () -> Unit) {
        FirebaseHelper.database.child("users/$uid/photo").setValue(photoUrl)
                .addOnFailureListener(setErrorOnFailureListener)
                .addOnSuccessListener { onSuccess() }
    }

    fun updateUser(updates: Map<String, Any?>, onSuccess: () -> Unit) {
        FirebaseHelper.database.child("users").child(uid).updateChildren(updates)
                .addOnFailureListener(setErrorOnFailureListener)
                .addOnSuccessListener { onSuccess() }
    }

    fun updateEmail(email: String, onSuccess: () -> Unit) {
        FirebaseHelper.auth.currentUser!!.updateEmail(email)
                .addOnFailureListener(setErrorOnFailureListener)
                .addOnSuccessListener { onSuccess() }
    }

    fun reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        FirebaseHelper.auth.currentUser!!.reauthenticate(credential)
                .addOnFailureListener(setErrorOnFailureListener)
                .addOnSuccessListener { onSuccess() }
    }
}
package com.alexbezhan.instagram.activities.profile.edit

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.ErrorMessageComponent
import com.alexbezhan.instagram.utils.FirebaseHelper
import com.alexbezhan.instagram.utils.FirebaseLiveData
import com.alexbezhan.instagram.utils.ProducesErrorMessage
import com.google.firebase.auth.AuthCredential
import com.google.firebase.storage.UploadTask

private val errorMessageComp = ErrorMessageComponent()

class EditProfileViewModel : ViewModel(), ProducesErrorMessage by errorMessageComp {
    val user: LiveData<User> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.currentUserReference()),
            {
                it.asUser()!!
            })

    private val uid = FirebaseHelper.currentUid()!!

    fun uploadUserPhoto(photo: Uri, onSuccess: (UploadTask.TaskSnapshot) -> Unit) {
        FirebaseHelper.storage.child("users/$uid/photo").putFile(photo)
                .addOnFailureListener(errorMessageComp.onFailureListener)
                .addOnSuccessListener(onSuccess)
    }

    fun updateUserPhoto(photoUrl: String, onSuccess: () -> Unit) {
        FirebaseHelper.database.child("users/$uid/photo").setValue(photoUrl)
                .addOnFailureListener(errorMessageComp.onFailureListener)
                .addOnSuccessListener { onSuccess() }
    }

    fun updateUser(updates: Map<String, Any?>, onSuccess: () -> Unit) {
        FirebaseHelper.database.child("users").child(uid).updateChildren(updates)
                .addOnFailureListener(errorMessageComp.onFailureListener)
                .addOnSuccessListener { onSuccess() }
    }

    fun updateEmail(email: String, onSuccess: () -> Unit) {
        FirebaseHelper.auth.currentUser!!.updateEmail(email)
                .addOnFailureListener(errorMessageComp.onFailureListener)
                .addOnSuccessListener { onSuccess() }
    }

    fun reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        FirebaseHelper.auth.currentUser!!.reauthenticate(credential)
                .addOnFailureListener(errorMessageComp.onFailureListener)
                .addOnSuccessListener { onSuccess() }
    }
}
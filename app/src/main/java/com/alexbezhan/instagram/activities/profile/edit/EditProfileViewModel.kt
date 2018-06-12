package com.alexbezhan.instagram.activities.profile.edit

import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.livedata.ErrorLiveDataComponent
import com.alexbezhan.instagram.utils.livedata.HasErrorLiveData
import com.alexbezhan.instagram.utils.livedata.HasUserLiveData
import com.alexbezhan.instagram.utils.livedata.UserLiveDataComponent
import com.google.firebase.auth.AuthCredential
import com.google.firebase.storage.UploadTask

private val errorComp = ErrorLiveDataComponent()

class EditProfileViewModel : ViewModel(), HasErrorLiveData by errorComp,
        HasUserLiveData by UserLiveDataComponent() {

    private val uid = FirebaseHelper.currentUid()!!

    fun uploadUserPhoto(photo: Uri, onSuccess: (UploadTask.TaskSnapshot) -> Unit) {
        FirebaseHelper.storage.child("users/$uid/photo").putFile(photo)
                .addOnFailureListener(errorComp.onFailureListener)
                .addOnSuccessListener(onSuccess)
    }

    fun updateUserPhoto(photoUrl: String, onSuccess: () -> Unit) {
        FirebaseHelper.database.child("users/$uid/photo").setValue(photoUrl)
                .addOnFailureListener(errorComp.onFailureListener)
                .addOnSuccessListener { onSuccess() }
    }

    fun updateUser(updates: Map<String, Any?>, onSuccess: () -> Unit) {
        FirebaseHelper.database.child("users").child(uid).updateChildren(updates)
                .addOnFailureListener(errorComp.onFailureListener)
                .addOnSuccessListener { onSuccess() }
    }

    fun updateEmail(email: String, onSuccess: () -> Unit) {
        FirebaseHelper.auth.currentUser!!.updateEmail(email)
                .addOnFailureListener(errorComp.onFailureListener)
                .addOnSuccessListener { onSuccess() }
    }

    fun reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        FirebaseHelper.auth.currentUser!!.reauthenticate(credential)
                .addOnFailureListener(errorComp.onFailureListener)
                .addOnSuccessListener { onSuccess() }
    }
}
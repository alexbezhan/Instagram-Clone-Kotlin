package com.alexbezhan.instagram.data.firebase

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.alexbezhan.instagram.data.ImagesRepository
import com.alexbezhan.instagram.data.firebase.utils.currentUid
import com.alexbezhan.instagram.data.firebase.utils.database
import com.alexbezhan.instagram.data.firebase.utils.storage
import com.alexbezhan.instagram.data.live.FirebaseLiveData
import com.alexbezhan.instagram.data.live.map
import com.alexbezhan.instagram.data.task
import com.alexbezhan.instagram.data.toUnit
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference

class FirebaseImagesRepository : ImagesRepository {
    override fun getImages(uid: String): LiveData<List<String>> =
            FirebaseLiveData { "images/$uid" }.map {
                it.children.map { it.getValue(String::class.java)!! }
            }

    override fun getImages(): LiveData<List<String>> =
            FirebaseLiveData { "images/$it" }.map {
                it.children.map { it.getValue(String::class.java)!! }
            }

    override fun uploadUserPhoto(photo: Uri): Task<Uri> =
            storage.child("users/${currentUid()}/photo").uploadFile(photo)

    override fun setUserPhotoUrl(photoUrl: Uri): Task<Unit> =
            database.child("users/${currentUid()}/photo").setValue(photoUrl.toString()).toUnit()

    override fun uploadUserImage(imageUri: Uri): Task<Uri> =
            storage.child("users/${currentUid()}/images/${imageUri.lastPathSegment}")
                    .uploadFile(imageUri)

    private fun StorageReference.uploadFile(file: Uri): Task<Uri> =
            task { taskSource ->
                putFile(file).addOnCompleteListener {
                    if (it.isSuccessful) {
                        taskSource.setResult(it.result.downloadUrl!!)
                    } else {
                        taskSource.setException(it.exception!!)
                    }
                }
            }

    override fun addUserImageUrl(imageUri: Uri): Task<Unit> =
            database.child("images/${currentUid()}").push().setValue(imageUri.toString()).toUnit()

}
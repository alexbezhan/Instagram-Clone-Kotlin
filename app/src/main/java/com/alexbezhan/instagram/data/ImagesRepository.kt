package com.alexbezhan.instagram.data

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.google.android.gms.tasks.Task

interface ImagesRepository {
    fun getImages(uid: String): LiveData<List<String>>
    fun getImages(): LiveData<List<String>>
    fun uploadUserImage(imageUri: Uri): Task<Uri>
    fun addUserImageUrl(imageUri: Uri): Task<Unit>
    fun uploadUserPhoto(photo: Uri): Task<Uri>
    fun setUserPhotoUrl(photoUrl: Uri): Task<Unit>
}
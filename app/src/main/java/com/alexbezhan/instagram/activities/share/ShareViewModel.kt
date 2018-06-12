package com.alexbezhan.instagram.activities.share

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.activities.task
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.*
import com.alexbezhan.instagram.utils.FirebaseHelper.database
import com.alexbezhan.instagram.utils.FirebaseHelper.storage
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

private val errorMessageComp = ErrorMessageComponent()

class ShareViewModel : ViewModel(), ProducesErrorMessage by errorMessageComp {
    val user: LiveData<User> = Transformations.map(
            FirebaseLiveData(FirebaseHelper.currentUserReference()),
            {
                it.asUser()!!
            })

    private val uid = FirebaseHelper.currentUid()!!

    fun share(imageUri: Uri, caption: String, user: User): Task<Void> {
        return task { taskSource ->
            storage.child("users").child(uid).child("images")
                    .child(imageUri.lastPathSegment).putFile(imageUri)
                    .addOnFailureListener(errorMessageComp.onFailureListener)
                    .addOnSuccessListener {
                        val imageDownloadUrl = it.downloadUrl!!.toString()

                        val addImage = database.child("images").child(uid).push()
                                .setValue(imageDownloadUrl)
                        val addFeedPost = database.child("feed-posts").child(uid)
                                .push()
                                .setValue(mkFeedPost(uid, imageDownloadUrl, caption, user))

                        Tasks.whenAll(addImage, addFeedPost)
                                .addOnFailureListener(errorMessageComp.onFailureListener)
                                .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                    }
        }
    }

    private fun mkFeedPost(uid: String, imageDownloadUrl: String, caption: String, user: User)
            : FeedPost {
        return FeedPost(
                uid = uid,
                username = user.username,
                image = imageDownloadUrl,
                caption = caption,
                photo = user.photo
        )
    }

}
package com.alexbezhan.instagram.activities.share

import android.net.Uri
import com.alexbezhan.instagram.SingleLiveEvent
import com.alexbezhan.instagram.activities.BaseViewModel
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.repository.Repository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class ShareViewModel(repository: Repository) : BaseViewModel(repository) {

    val openProfileUiCmd = SingleLiveEvent<Unit>()

    fun share(localImageUri: Uri?, caption: String, user: User) {
        fun mkFeedPost(imageDownloadUrl: String)
                : FeedPost {
            return FeedPost(
                    uid = repository.currentUid()!!,
                    username = user.username,
                    image = imageDownloadUrl,
                    caption = caption,
                    photo = user.photo
            )
        }

        if (localImageUri != null && caption.isNotEmpty()) {
            val uploadUserImage: Task<Uri> = repository.uploadUserImage(localImageUri)
            uploadUserImage.onSuccessTask { remoteImageUri ->
                val addImage = repository.addUserImageUrl(remoteImageUri!!)
                val addFeedPost = with(mkFeedPost(remoteImageUri.toString())) {
                    repository.addFeedPost(uid, this)
                }
                Tasks.whenAll(addImage, addFeedPost)
                        .addOnFailureListener(setErrorOnFailureListener)
                        .addOnSuccessListener {
                            openProfileUiCmd.call()
                        }
            }
        }
    }
}
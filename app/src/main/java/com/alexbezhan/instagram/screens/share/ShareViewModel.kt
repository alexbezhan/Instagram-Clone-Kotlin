package com.alexbezhan.instagram.screens.share

import android.arch.lifecycle.ViewModel
import android.net.Uri
import android.util.Log
import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.data.live.SingleLiveEvent
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.screens.common.CommonLiveData
import com.google.android.gms.tasks.Tasks

class ShareViewModel(private val repository: Repository, liveData: CommonLiveData)
    : ViewModel(), CommonLiveData by liveData {

    private val TAG = "ShareViewModel"
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
            Log.d(TAG, "uploadUserImage")
            repository.uploadUserImage(localImageUri)
                    .addOnFailureListener(setErrorOnFailureListener)
                    .onSuccessTask { remoteImageUri ->
                        val addImage = repository.addUserImageUrl(remoteImageUri!!)
                        val addFeedPost = with(mkFeedPost(remoteImageUri.toString())) {
                            repository.addFeedPost(uid, this)
                        }
                        Log.d(TAG, "addImage and addFeedPost")
                        Tasks.whenAll(addImage, addFeedPost)
                                .addOnFailureListener(setErrorOnFailureListener)
                                .addOnSuccessListener {
                                    openProfileUiCmd.call()
                                }
                    }
        }
    }
}
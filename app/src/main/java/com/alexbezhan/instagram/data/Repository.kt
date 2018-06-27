package com.alexbezhan.instagram.data

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.alexbezhan.instagram.models.*
import com.google.android.gms.tasks.Task

interface Repository {
    fun signIn(email: String, password: String): Task<Unit>
    fun getFeedPosts(): LiveData<List<FeedPost>>
    fun getFeedPost(uid: String, postId: String): LiveData<FeedPost>
    fun getComments(postId: String): LiveData<List<Comment>>
    fun getUser(uid: String): LiveData<User>
    fun getUser(): LiveData<User>
    fun createComment(postId: String, comment: Comment): Task<String>
    fun setNotificationsRead(notificationsIds: List<String>, read: Boolean): Task<Unit>
    fun getImages(uid: String): LiveData<List<String>>
    fun getImages(): LiveData<List<String>>
    fun updateUserProfile(newUser: User, existingUser: User): Task<Unit>
    fun updateUserEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    fun getUsers(): LiveData<List<User>>
    fun signOut()
    fun isUserExistsByEmail(email: String): Task<Boolean>
    fun createUser(user: User, password: String): Task<Unit>
    fun uploadUserPhoto(photo: Uri): Task<Uri>
    fun setUserPhotoUrl(photoUrl: Uri): Task<Unit>
    fun uploadUserImage(imageUri: Uri): Task<Uri>
    fun addUserImageUrl(imageUri: Uri): Task<Unit>
    fun addFeedPost(uid: String, post: FeedPost): Task<Unit>
    fun notifications(): LiveData<List<Notification>>
    fun likes(postId: String): LiveData<List<FeedPostLike>>
    fun commentsCount(postId: String): LiveData<Int>
    fun currentUid(): String?
    fun authState(): LiveData<String>
    fun getCurrentUserFeedPost(postId: String): LiveData<FeedPost>
    fun getUserFollowsValue(uid: String, toUid: String): Task<String?>
    fun addNotification(toUid: String, notification: Notification): Task<String>
    fun removeNotification(toUid: String, id: String): Task<Void>
    fun getLikeValue(postId: String, uid: String): Task<String?>
    fun setLikeValue(postId: String, uid: String, notificationId: String): Task<Void>
    fun deleteLikeValue(postId: String, uid: String): Task<Void>
    fun setUserFollowsValue(uid: String, toUid: String, notificationId: String): Task<Void>
    fun deleteUserFollows(uid: String, toUid: String): Task<Void>
    fun setUserFollowersValue(uid: String, fromUid: String, notificationId: String): Task<Void>
    fun getUserFollowersValue(uid: String, fromUid: String): Task<String?>
    fun deleteUserFollowers(uid: String, fromUid: String): Task<Void>
    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Void>
    fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Void>
}
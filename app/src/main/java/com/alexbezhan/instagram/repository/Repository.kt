package com.alexbezhan.instagram.repository

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.alexbezhan.instagram.activities.*
import com.alexbezhan.instagram.models.*
import com.alexbezhan.instagram.utils.firebase.FirebaseAuthStateLiveData
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.auth
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.storage
import com.alexbezhan.instagram.utils.firebase.TaskSourceOnCompleteListener
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider

interface Repository {
    fun signIn(email: String, password: String): Task<Unit>
    fun getFeedPosts(uid: String): LiveData<List<FeedPost>>
    fun getFeedPost(uid: String, postId: String): LiveData<FeedPost>
    fun getComments(postId: String): LiveData<List<Comment>>
    fun getUser(uid: String): LiveData<User>
    fun createComment(postId: String, comment: Comment): Task<String>
    fun setNotificationsRead(uid: String, notificationsIds: List<String>, read: Boolean): Task<Unit>
    fun getImages(uid: String): LiveData<List<String>>
    fun updateUserProfile(uid: String, user: User): Task<Unit>
    fun updateUserEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    fun getUsers(): LiveData<List<User>>
    fun signOut()
    fun isUserExistsByEmail(email: String): Task<Boolean>
    fun createUser(user: User, password: String): Task<Unit>
    fun uploadUserPhoto(uid: String, photo: Uri): Task<Uri>
    fun setUserPhotoUrl(uid: String, photoUrl: Uri): Task<Unit>
    fun uploadUserImage(uid: String, imageUri: Uri): Task<Uri>
    fun addUserImageUrl(uid: String, imageUri: Uri): Task<Unit>
    fun addFeedPost(uid: String, post: FeedPost): Task<Unit>
    fun notifications(): LiveData<List<Notification>>
    fun likes(postId: String): LiveData<List<FeedPostLike>>
    fun commentsCount(postId: String): LiveData<Int>
    fun currentUid(): String?
    fun authState(): LiveData<String>
}

class FirebaseRepository : Repository {
    override fun authState(): LiveData<String> = FirebaseAuthStateLiveData()

    override fun currentUid(): String? = FirebaseHelper.currentUid()

    override fun likes(postId: String): LiveData<List<FeedPostLike>> =
            FirebaseLiveData { "likes/$postId" }.map {
                it.children.map {
                    FeedPostLike(
                            userId = it.key,
                            notificationId = it.getValue(String::class.java)!!)
                }
            }

    override fun commentsCount(postId: String): LiveData<Int> =
            FirebaseLiveData { "comments/$postId" }.map {
                it.children.count()
            }

    override fun notifications(): LiveData<List<Notification>> =
            FirebaseLiveData { "notifications/$it" }.map {
                it.children.map { it.asNotification()!! }
            }

    override fun createUser(user: User, password: String): Task<Unit> =
            FirebaseHelper.auth.createUserWithEmailAndPassword(user.email, password)
                    .onSuccessTask {
                        database.child("users").child(it!!.user.uid).setValue(user)
                                .toUnit()
                    }

    override fun isUserExistsByEmail(email: String): Task<Boolean> =
            FirebaseHelper.auth.fetchSignInMethodsForEmail(email).onSuccessTask {
                val signInMethods = it?.signInMethods ?: emptyList()
                Tasks.forResult(signInMethods.isEmpty())
            }

    override fun signOut() {
        FirebaseHelper.auth.signOut()
    }

    override fun getUsers(): LiveData<List<User>> =
            FirebaseLiveData { "users" }.map {
                it.children.map { it.asUser()!! }
            }

    override fun getFeedPosts(uid: String): LiveData<List<FeedPost>> =
            FirebaseLiveData { "feed-posts/$uid" }.map {
                it.children.map { it.asFeedPost()!! }
            }

    override fun getComments(postId: String): LiveData<List<Comment>> =
            FirebaseLiveData { "comments/$postId" }.map {
                it.children.map { it.getValue(Comment::class.java)!! }
            }

    override fun getFeedPost(uid: String, postId: String): LiveData<FeedPost> =
            FirebaseLiveData { "feed-posts/$uid/$postId" }.map {
                it.asFeedPost()!!
            }

    override fun getUser(uid: String): LiveData<User> =
            FirebaseLiveData { "users/$uid" }.map {
                it.asUser()!!
            }

    override fun createComment(postId: String, comment: Comment): Task<String> =
            task { taskSource ->
                val commentRef = database.child("comments").child(postId).push()
                commentRef.setValue(comment)
                        .onSuccessTask { Tasks.forResult(commentRef.key) }
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
            }

    override fun signIn(email: String, password: String): Task<Unit> =
            task { taskSource ->
                auth.signInWithEmailAndPassword(email, password)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
            }

    override fun setNotificationsRead(uid: String, notificationsIds: List<String>,
                                      read: Boolean): Task<Unit> {
        val updatesMap = notificationsIds.map { "/$it/read" to read }.toMap()
        return database.child("notifications").child(uid)
                .updateChildren(updatesMap)
                .toUnit()
    }

    override fun getImages(uid: String): LiveData<List<String>> =
            FirebaseLiveData { "images/$uid" }.map {
                it.children.map { it.getValue(String::class.java)!! }
            }

    override fun uploadUserPhoto(uid: String, photo: Uri): Task<Uri> =
            storage.child("users/$uid/photo").putFile(photo)
                    .onSuccessTask { it ->
                        Tasks.forResult(it!!.downloadUrl!!)
                    }

    override fun setUserPhotoUrl(uid: String, photoUrl: Uri): Task<Unit> =
            database.child("users/$uid/photo").setValue(photoUrl.toString())
                    .toUnit()

    override fun uploadUserImage(uid: String, imageUri: Uri): Task<Uri> =
            storage.child("users/$uid/images/${imageUri.lastPathSegment}")
                    .putFile(imageUri)
                    .onSuccessTask { Tasks.forResult(it!!.downloadUrl!!) }

    override fun addUserImageUrl(uid: String, imageUri: Uri): Task<Unit> =
            database.child("images/$uid").push().setValue(imageUri).toUnit()

    override fun addFeedPost(uid: String, post: FeedPost): Task<Unit> =
            database.child("feed-posts/$uid").push().setValue(post).toUnit()

    override fun updateUserProfile(uid: String, user: User): Task<Unit> {
        val updatesMap = mutableMapOf<String, Any?>()
        if (user.name != user.name) updatesMap["name"] = user.name
        if (user.username != user.username) updatesMap["username"] = user.username
        if (user.website != user.website) updatesMap["website"] = user.website
        if (user.bio != user.bio) updatesMap["bio"] = user.bio
        if (user.email != user.email) updatesMap["email"] = user.email
        if (user.phone != user.phone) updatesMap["phone"] = user.phone

        return database.child("users/$uid").updateChildren(updatesMap).toUnit()
    }

    override fun updateUserEmail(currentEmail: String, newEmail: String,
                                 password: String): Task<Unit> {
        val credential = EmailAuthProvider.getCredential(currentEmail, password)
        with(auth.currentUser) {
            return this?.reauthenticate(credential)?.onSuccessTask {
                this.updateEmail(newEmail).toUnit()
            } ?: Tasks.forException(IllegalStateException("User is unauthenticated"))
        }
    }
}

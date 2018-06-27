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
import com.alexbezhan.instagram.utils.firebase.ValueEventListenerAdapter
import com.alexbezhan.instagram.utils.livedata.FirebaseLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

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

class FirebaseRepository : Repository {
    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Void> =
            task { taskSource ->
                database.child("feed-posts").child(postsAuthorUid)
                        .orderByChild("uid")
                        .equalTo(postsAuthorUid)
                        .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                            val postsMap = it.children.map { it.key to it.value }.toMap()
                            FirebaseHelper.database.child("feed-posts")
                                    .child(uid).updateChildren(postsMap)
                                    .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                        })
            }

    override fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Void> =
            task { taskSource ->
                database.child("feed-posts").child(uid)
                        .orderByChild("uid")
                        .equalTo(postsAuthorUid)
                        .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                            val postsMap = it.children.map { it.key to null }.toMap()
                            FirebaseHelper.database.child("feed-posts")
                                    .child(uid).updateChildren(postsMap)
                                    .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                        })
            }

    override fun setLikeValue(postId: String, uid: String, notificationId: String): Task<Void> =
            getLikeRef(postId, uid).setValue(notificationId)

    override fun deleteLikeValue(postId: String, uid: String): Task<Void> =
            getLikeRef(postId, uid).removeValue()

    override fun getLikeValue(postId: String, uid: String): Task<String?> =
            getRefValue(getLikeRef(postId, uid))
                    .onSuccessTask { Tasks.forResult(it?.getValue(String::class.java)) }

    override fun getUserFollowsValue(uid: String, toUid: String): Task<String?> =
            getRefValue(getUserFollowsRef(uid, toUid))
                    .onSuccessTask { Tasks.forResult(it?.getValue(String::class.java)) }

    override fun setUserFollowsValue(uid: String, toUid: String, notificationId: String): Task<Void> =
            getUserFollowsRef(uid, toUid).setValue(notificationId)

    override fun getUserFollowersValue(uid: String, fromUid: String): Task<String?> =
            getRefValue(getUserFollowersRef(uid, fromUid))
                    .onSuccessTask { Tasks.forResult(it?.getValue(String::class.java)) }

    override fun setUserFollowersValue(uid: String, fromUid: String, notificationId: String): Task<Void> =
            getUserFollowersRef(uid, fromUid).setValue(notificationId)

    override fun deleteUserFollows(uid: String, toUid: String): Task<Void> =
            getUserFollowsRef(uid, toUid).removeValue()

    override fun deleteUserFollowers(uid: String, fromUid: String): Task<Void> =
            getUserFollowersRef(uid, fromUid).removeValue()

    private fun getUserFollowsRef(uid: String, toUid: String) =
            database.child("users/$uid/follows/$toUid")

    private fun getUserFollowersRef(uid: String, fromUid: String) =
            database.child("users/$uid/followers/$fromUid")

    private fun getLikeRef(postId: String, uid: String) =
            database.child("likes").child(postId).child(uid)

    private fun getRefValue(ref: DatabaseReference): Task<DataSnapshot> = task { taskSource ->
        ref.addListenerForSingleValueEvent(ValueEventListenerAdapter {
            taskSource.setResult(it)
        })
    }

    override fun removeNotification(toUid: String, id: String): Task<Void> =
            getNotificationsRef(toUid).child(id).removeValue()

    override fun addNotification(toUid: String, notification: Notification): Task<String> {
        val ref = getNotificationsRef(toUid).push()
        return ref.setValue(notification).onSuccessTask {
            Tasks.forResult(ref.key)
        }
    }

    private fun getNotificationsRef(uid: String) =
            FirebaseHelper.database.child("notifications").child(uid)

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

    override fun getFeedPosts(): LiveData<List<FeedPost>> =
            FirebaseLiveData { "feed-posts/$it" }.map {
                it.children.map { it.asFeedPost()!! }
            }

    override fun getComments(postId: String): LiveData<List<Comment>> =
            FirebaseLiveData { "comments/$postId" }.map {
                it.children.map { it.getValue(Comment::class.java)!! }
            }

    override fun getCurrentUserFeedPost(postId: String): LiveData<FeedPost> =
            FirebaseLiveData { "feed-posts/$it/$postId" }.map {
                it.asFeedPost()!!
            }

    override fun getFeedPost(uid: String, postId: String): LiveData<FeedPost> =
            FirebaseLiveData { "feed-posts/$uid/$postId" }.map {
                it.asFeedPost()!!
            }

    override fun getUser(): LiveData<User> =
            FirebaseLiveData { "users/$it" }.map {
                it.asUser()!!
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

    override fun setNotificationsRead(notificationsIds: List<String>,
                                      read: Boolean): Task<Unit> {
        val updatesMap = notificationsIds.map { "/$it/read" to read }.toMap()
        return database.child("notifications/${currentUid()}")
                .updateChildren(updatesMap)
                .toUnit()
    }

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

    override fun addFeedPost(uid: String, post: FeedPost): Task<Unit> =
            database.child("feed-posts/$uid").push().setValue(post).toUnit()

    override fun updateUserProfile(newUser: User, existingUser: User): Task<Unit> {
        val updatesMap = mutableMapOf<String, Any?>()
        if (newUser.name != existingUser.name) updatesMap["name"] = newUser.name
        if (newUser.username != existingUser.username) updatesMap["username"] = newUser.username
        if (newUser.website != existingUser.website) updatesMap["website"] = newUser.website
        if (newUser.bio != existingUser.bio) updatesMap["bio"] = newUser.bio
        if (newUser.email != existingUser.email) updatesMap["email"] = newUser.email
        if (newUser.phone != existingUser.phone) updatesMap["phone"] = newUser.phone

        return database.child("users/${currentUid()}").updateChildren(updatesMap).toUnit()
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

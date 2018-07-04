package com.alexbezhan.instagram.data.firebase

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.alexbezhan.instagram.common.task
import com.alexbezhan.instagram.common.toUnit
import com.alexbezhan.instagram.data.UsersRepository
import com.alexbezhan.instagram.data.common.map
import com.alexbezhan.instagram.data.firebase.common.*
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class FirebaseUsersRepository : UsersRepository {
    override fun createFeedPost(uid: String, feedPost: FeedPost): Task<Unit> =
        database.child("feed-posts").child(uid)
                .push().setValue(feedPost).toUnit()

    override fun setUserImage(uid: String, downloadUri: Uri): Task<Unit> =
        database.child("images").child(uid).push()
                .setValue(downloadUri.toString()).toUnit()

    override fun uploadUserImage(uid: String, imageUri: Uri): Task<Uri> =
            task { taskSource ->
                storage.child("users").child(uid).child("images")
                        .child(imageUri.lastPathSegment).putFile(imageUri).addOnCompleteListener {
                            if (it.isSuccessful) {
                                taskSource.setResult(it.result.downloadUrl)
                            } else {
                                taskSource.setException(it.exception!!)
                            }
                        }
            }

    override fun createUser(user: User, password: String): Task<Unit> =
            auth.createUserWithEmailAndPassword(user.email, password).onSuccessTask {
                database.child("users").child(it!!.user.uid).setValue(user)
            }.toUnit()

    override fun isUserExistsForEmail(email: String): Task<Boolean> =
            auth.fetchSignInMethodsForEmail(email).onSuccessTask {
                val signInMethods = it?.signInMethods ?: emptyList<String>()
                Tasks.forResult(signInMethods.isNotEmpty())
            }

    override fun getImages(uid: String): LiveData<List<String>> =
            FirebaseLiveData(database.child("images").child(uid)).map {
                it.children.map { it.getValue(String::class.java)!! }
            }

    override fun getUsers(): LiveData<List<User>> =
            database.child("users").liveData().map {
                it.children.map { it.asUser()!! }
            }

    override fun addFollow(fromUid: String, toUid: String): Task<Unit> =
            getFollowsRef(fromUid, toUid).setValue(true).toUnit()

    override fun deleteFollow(fromUid: String, toUid: String): Task<Unit> =
            getFollowsRef(fromUid, toUid).removeValue().toUnit()

    override fun addFollower(fromUid: String, toUid: String): Task<Unit> =
            getFollowersRef(fromUid, toUid).setValue(true).toUnit()

    override fun deleteFollower(fromUid: String, toUid: String): Task<Unit> =
            getFollowersRef(fromUid, toUid).removeValue().toUnit()

    private fun getFollowsRef(fromUid: String, toUid: String) =
            database.child("users").child(fromUid).child("follows").child(toUid)

    private fun getFollowersRef(fromUid: String, toUid: String) =
            database.child("users").child(toUid).child("followers").child(fromUid)

    override fun currentUid() = FirebaseAuth.getInstance().currentUser?.uid

    override fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> {
        val updatesMap = mutableMapOf<String, Any?>()
        if (newUser.name != currentUser.name) updatesMap["name"] = newUser.name
        if (newUser.username != currentUser.username) updatesMap["username"] = newUser.username
        if (newUser.website != currentUser.website) updatesMap["website"] = newUser.website
        if (newUser.bio != currentUser.bio) updatesMap["bio"] = newUser.bio
        if (newUser.email != currentUser.email) updatesMap["email"] = newUser.email
        if (newUser.phone != currentUser.phone) updatesMap["phone"] = newUser.phone

        return database.child("users").child(currentUid()!!).updateChildren(updatesMap).toUnit()
    }

    override fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(currentEmail, password)
            currentUser.reauthenticate(credential).onSuccessTask {
                currentUser.updateEmail(newEmail)
            }.toUnit()
        } else {
            Tasks.forException(IllegalStateException("User is not authenticated"))
        }
    }

    override fun uploadUserPhoto(localImage: Uri): Task<Uri> =
            task { taskSource ->
                storage.child("users/${currentUid()!!}/photo").putFile(localImage)
                        .addOnSuccessListener {
                            taskSource.setResult(it?.downloadUrl!!)
                        }
            }

    override fun updateUserPhoto(downloadUrl: Uri): Task<Unit> =
            database.child("users/${currentUid()!!}/photo").setValue(downloadUrl.toString()).toUnit()

    override fun getUser(): LiveData<User> =
            database.child("users").child(currentUid()!!).liveData().map {
                it.asUser()!!
            }

}
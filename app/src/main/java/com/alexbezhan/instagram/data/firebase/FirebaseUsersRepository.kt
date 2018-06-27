package com.alexbezhan.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.UsersRepository
import com.alexbezhan.instagram.data.firebase.utils.*
import com.alexbezhan.instagram.data.live.FirebaseLiveData
import com.alexbezhan.instagram.data.live.map
import com.alexbezhan.instagram.data.toUnit
import com.alexbezhan.instagram.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DataSnapshot

class FirebaseUsersRepository : UsersRepository {
    override fun getUserFollowsValue(uid: String, toUid: String): Task<String?> =
            getRefValue(getUserFollowsRef(uid, toUid))
                    .onSuccessTask { Tasks.forResult(it?.asString()) }

    override fun setUserFollowsValue(uid: String, toUid: String, notificationId: String): Task<Void> =
            getUserFollowsRef(uid, toUid).setValue(notificationId)

    override fun getUserFollowersValue(uid: String, fromUid: String): Task<String?> =
            getRefValue(getUserFollowersRef(uid, fromUid))
                    .onSuccessTask { Tasks.forResult(it?.asString()) }

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

    override fun createUser(user: User, password: String): Task<Unit> =
            auth.createUserWithEmailAndPassword(user.email, password)
                    .onSuccessTask {
                        database.child("users").child(it!!.user.uid).setValue(user)
                                .toUnit()
                    }

    override fun isUserExistsByEmail(email: String): Task<Boolean> =
            auth.fetchSignInMethodsForEmail(email).onSuccessTask {
                val signInMethods = it?.signInMethods ?: emptyList()
                Tasks.forResult(signInMethods.isEmpty())
            }

    override fun getUsers(): LiveData<List<User>> =
            FirebaseLiveData { "users" }.map {
                it.children.map { it.asUser()!! }
            }

    override fun getUser(): LiveData<User> =
            FirebaseLiveData { "users/$it" }.map {
                it.asUser()!!
            }

    override fun getUser(uid: String): LiveData<User> =
            FirebaseLiveData { "users/$uid" }.map {
                it.asUser()!!
            }

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

fun DataSnapshot.asUser(): User? =
        getValue(User::class.java)?.copy(uid = key)
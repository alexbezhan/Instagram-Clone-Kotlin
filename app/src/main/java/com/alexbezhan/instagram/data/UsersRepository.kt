package com.alexbezhan.instagram.data

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.models.User
import com.google.android.gms.tasks.Task

interface UsersRepository {
    fun getUser(uid: String): LiveData<User>
    fun getUser(): LiveData<User>
    fun getUsers(): LiveData<List<User>>
    fun updateUserProfile(newUser: User, existingUser: User): Task<Unit>
    fun updateUserEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    fun createUser(user: User, password: String): Task<Unit>
    fun isUserExistsByEmail(email: String): Task<Boolean>
    fun getUserFollowsValue(uid: String, toUid: String): Task<String?>
    fun setUserFollowsValue(uid: String, toUid: String, notificationId: String): Task<Void>
    fun deleteUserFollows(uid: String, toUid: String): Task<Void>
    fun setUserFollowersValue(uid: String, fromUid: String, notificationId: String): Task<Void>
    fun getUserFollowersValue(uid: String, fromUid: String): Task<String?>
    fun deleteUserFollowers(uid: String, fromUid: String): Task<Void>
}
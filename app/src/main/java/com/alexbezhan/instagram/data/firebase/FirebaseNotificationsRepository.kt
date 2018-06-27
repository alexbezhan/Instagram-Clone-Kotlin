package com.alexbezhan.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.NotificationsRepository
import com.alexbezhan.instagram.data.firebase.utils.FirebaseHelper
import com.alexbezhan.instagram.data.firebase.utils.FirebaseHelper.currentUid
import com.alexbezhan.instagram.data.live.FirebaseLiveData
import com.alexbezhan.instagram.data.live.map
import com.alexbezhan.instagram.data.toUnit
import com.alexbezhan.instagram.models.Notification
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot

class FirebaseNotificationsRepository : NotificationsRepository {
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

    override fun notifications(): LiveData<List<Notification>> =
            FirebaseLiveData { "notifications/$it" }.map {
                it.children.map { it.asNotification()!! }
            }

    override fun setNotificationsRead(notificationsIds: List<String>,
                                      read: Boolean): Task<Unit> {
        val updatesMap = notificationsIds.map { "/$it/read" to read }.toMap()
        return FirebaseHelper.database.child("notifications/${currentUid()}")
                .updateChildren(updatesMap)
                .toUnit()
    }

}

fun DataSnapshot.asNotification(): Notification? =
        getValue(Notification::class.java)?.copy(id = key)
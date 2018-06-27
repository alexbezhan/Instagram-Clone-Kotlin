package com.alexbezhan.instagram.data

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.models.Notification
import com.google.android.gms.tasks.Task

interface NotificationsRepository {
    fun setNotificationsRead(notificationsIds: List<String>, read: Boolean): Task<Unit>
    fun notifications(): LiveData<List<Notification>>
    fun addNotification(toUid: String, notification: Notification): Task<String>
    fun removeNotification(toUid: String, id: String): Task<Void>
}
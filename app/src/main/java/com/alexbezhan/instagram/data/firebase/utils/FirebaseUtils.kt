package com.alexbezhan.instagram.data.firebase.utils

import com.alexbezhan.instagram.data.task
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

val auth: FirebaseAuth = FirebaseAuth.getInstance()

val database: DatabaseReference = with(FirebaseDatabase.getInstance()) {
    setPersistenceEnabled(false) // temporarily for development stage
    reference
}

val storage: StorageReference = FirebaseStorage.getInstance().reference

fun currentUid(): String? = auth.currentUser?.uid

fun getRefValue(ref: DatabaseReference): Task<DataSnapshot> = task { taskSource ->
    ref.addListenerForSingleValueEvent(ValueEventListenerAdapter {
        taskSource.setResult(it)
    })
}

fun DataSnapshot.asString(): String? =
        getValue(String::class.java)
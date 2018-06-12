package com.alexbezhan.instagram.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FirebaseHelper {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference
    val storage: StorageReference = FirebaseStorage.getInstance().reference

    init {
        val db = FirebaseDatabase.getInstance()
        db.setPersistenceEnabled(true)
        database = db.reference
    }

    fun currentUserReference(): DatabaseReference =
            database.child("users").child(currentUid()!!)

    fun currentUid(): String? =
            auth.currentUser?.uid

}
package com.alexbezhan.instagram.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


val auth: FirebaseAuth = FirebaseAuth.getInstance()

val database: DatabaseReference = FirebaseDatabase.getInstance().reference

val storage: StorageReference = FirebaseStorage.getInstance().reference

fun currentUid(): String? = auth.currentUser?.uid
package com.alexbezhan.instagram.data.firebase

import com.alexbezhan.instagram.data.firebase.utils.ValueEventListenerAdapter
import com.alexbezhan.instagram.data.task
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

fun getRefValue(ref: DatabaseReference): Task<DataSnapshot> = task { taskSource ->
    ref.addListenerForSingleValueEvent(ValueEventListenerAdapter {
        taskSource.setResult(it)
    })
}

fun DataSnapshot.asString(): String? =
        getValue(String::class.java)
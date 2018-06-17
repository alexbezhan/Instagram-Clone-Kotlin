package com.alexbezhan.instagram

import com.alexbezhan.instagram.utils.firebase.ValueEventListenerAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun Query.readOnce(): DataSnapshot = readN(1).first()

fun Query.readN(count: Int): List<DataSnapshot> {
    var listener: ValueEventListener? = null
    val result = awaitN<DataSnapshot>(count) { latch ->
        listener = ValueEventListenerAdapter {
            latch.addValue(it)
        }
        addValueEventListener(listener)
    }
    listener?.let { removeEventListener(it) }
    return result
}

fun <T> Task<T>.await(): T {
    val task = awaitN<Task<T>>(1) { latch ->
        addOnCompleteListener { latch.addValue(it) }
    }.first()
    if (task.isSuccessful) {
        return task.result
    } else {
        throw task.exception!!
    }
}

fun <T> awaitN(count: Int, f: (ListValueLatch<T>) -> Unit): List<T> {
    val latchWithValue = ListValueLatch<T>(count)
    f(latchWithValue)
    latchWithValue.countDownLatch.await(5, TimeUnit.SECONDS)
    return latchWithValue.valueList
}

class ListValueLatch<T>(count: Int = 1) {
    val countDownLatch = CountDownLatch(count)
    var valueList: List<T> = emptyList()

    fun addValue(value: T) {
        if (countDownLatch.count != 0L) {
            valueList += value
            countDownLatch.countDown()
        }
    }
}
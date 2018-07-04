package com.alexbezhan.instagram.common

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks

fun <T> task(block: (TaskCompletionSource<T>) -> Unit): Task<T> {
    val taskSource = TaskCompletionSource<T>()
    block(taskSource)
    return taskSource.task
}

fun Task<*>.toUnit(): Task<Unit> =
        onSuccessTask { Tasks.forResult(Unit) }
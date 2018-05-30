package com.alexbezhan.instagram.utils

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource

class TaskSourceOnCompleteListener<T>(private val taskSource: TaskCompletionSource<T>) :
        OnCompleteListener<T> {
    override fun onComplete(task: Task<T>) {
        if (task.isSuccessful) {
            taskSource.setResult(task.result)
        } else {
            taskSource.setException(task.exception!!)
        }
    }

}
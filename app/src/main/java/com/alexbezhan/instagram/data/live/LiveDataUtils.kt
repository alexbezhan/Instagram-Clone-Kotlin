package com.alexbezhan.instagram.data.live

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations

fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null)
                this.value = Pair(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}

fun <A, B> LiveData<A>.zip(b: LiveData<B>): LiveData<Pair<A, B>> =
        zipLiveData(this, b)

fun <A, B> LiveData<A>.map(function: (A) -> B): LiveData<B> =
        Transformations.map(this, function)
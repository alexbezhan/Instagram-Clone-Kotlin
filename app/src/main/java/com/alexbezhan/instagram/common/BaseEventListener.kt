package com.alexbezhan.instagram.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

abstract class BaseEventListener : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}
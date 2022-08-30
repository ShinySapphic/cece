package com.nexus.nexusnpcs.ecs

abstract class AbstractSystem @JvmOverloads constructor(val query: Query, internal val priority: Int = 0) {
    open fun update(deltaTime: Float) {

    }
}
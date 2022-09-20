package me.lucidus.cece

abstract class EcsSystem constructor(vararg query: Query) {
    val queries = query

    open fun update(deltaTime: Float) {

    }
}
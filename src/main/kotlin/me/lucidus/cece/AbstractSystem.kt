package me.lucidus.cece

abstract class AbstractSystem constructor(vararg query: Query) {
    val queries = query

    open fun update(deltaTime: Float) {

    }
}
package me.lucidus.cece

abstract class EcsSystem constructor(query: Query, vararg queries: Query) {
    val queries = arrayOf(query, *queries)

    open fun update(deltaTime: Float) {

    }
}
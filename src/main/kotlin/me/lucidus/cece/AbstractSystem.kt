package me.lucidus.cece

abstract class AbstractSystem constructor(vararg query: Query) {

    val queries: Array<Query> = arrayOf(*query)

    open fun update(deltaTime: Float) {

    }
}
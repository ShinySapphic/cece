package me.lucidus.cece

abstract class EcsSystem constructor(query: Query, vararg queries: Query) {

    @get:JvmSynthetic
    internal val queries = arrayOf(query, *queries)

    open fun update(deltaTime: Float) {

    }

    fun query(index: Int) : Query {
        return queries[index]
    }
}
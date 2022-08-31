package me.lucidus.cece

abstract class AbstractSystem @JvmOverloads constructor(val query: Query, val priority: Int = 0) {
    open fun update(deltaTime: Float) {

    }

    internal fun onAddedToEngine(engine: Engine) {
        query.populate(engine)
    }
}
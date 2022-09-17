package me.lucidus.cece

abstract class AbstractSystem constructor(val query: Query) {
    open fun update(deltaTime: Float) {

    }

    internal fun onAddedToEngine(engine: Engine) {
        query.populate(engine)
    }
}
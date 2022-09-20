package me.lucidus.cece

open class Entity(private val engine: Engine) {
    val id = count++
    companion object {
        var count = 0u
    }

    fun addComponent(component: Component): Entity {
        engine.addComponent(this, component)
        return this
    }

    fun removeComponent(componentClass: ComponentClass): Entity {
        engine.removeComponent(this, componentClass)
        return this
    }

    fun hasComponent(componentClass: ComponentClass): Boolean {
        return engine.hasComponent(this, componentClass)
    }

    fun <T : Component?> getComponent(componentClass: ComponentClass): T? {
        return engine.getComponent(this, componentClass)
    }
}
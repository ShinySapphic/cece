package me.lucidus.cece

open class Entity(private val engine: Engine) {
    val id = count++

    fun addComponent(component: Component): Entity {
        Engine.addComponent(this, component)
        return this
    }

    fun removeComponent(componentClass: ComponentClass): Entity {
        Engine.removeComponent(this, componentClass)
        return this
    }

    fun hasComponent(componentClass: ComponentClass): Boolean {
        return Engine.hasComponent(this, componentClass)
    }

    fun <T : Component?> getComponent(componentClass: ComponentClass): T? {
        return Engine.getComponent(this, componentClass)
    }

    companion object {
        var count = 0
    }
}
package me.lucidus.cece

class EntityRef(private val engine: Engine, val id: UInt) {

    fun addComponent(component: Component): EntityRef {
        engine.addComponent(this, component)
        return this
    }

    fun removeComponent(componentClass: ComponentClass): EntityRef {
        engine.removeComponent(this, ComponentType.getFor(componentClass).id)
        return this
    }

    fun hasComponent(componentClass: ComponentClass): Boolean {
        return engine.hasComponent(this, ComponentType.getFor(componentClass).id)
    }

    fun <T : Component> getComponent(componentClass: Class<T>): T? {
        return engine.getComponent(this, ComponentType.getFor(componentClass).id)
    }

    /**
     * Removes this entity from the engine.
     */
    fun remove() {
        engine.removeEntity(this)
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntityRef) return false

        if (engine != other.engine) return false
        if (id != other.id) return false

        return true
    }
    
    override fun hashCode(): Int {
        var result = engine.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}

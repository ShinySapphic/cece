package me.lucidus.cece

class Query private constructor(private val include: MutableList<ComponentClass>, private val exclude: MutableList<ComponentClass>) : Iterable<Entity> {
    private val entities = mutableListOf<Entity>()

    fun contains(entity: Entity): Boolean {
        return entities.contains(entity)
    }

    /**
     * Populates this query from its parameters
     */
    fun populate(engine: Engine) {
        for (ent in engine.getEntities())
            validate(ent)
    }

    /**
     * Checks that this entity meets the query's requirements and adds to the list if so.
     * If this entity has been modified and no longer meets requirements, it will be removed.
     */
    fun validate(entity: Entity) {
        for (comp in exclude) {
            if (entity.hasComponent(comp)) {
                entities.remove(entity)
                return
            }
        }

        if (hasComponents(entity, include)) {
            if (!entities.contains(entity))
                entities.add(entity)
        } else
            entities.remove(entity)
    }

    private fun hasComponents(entity: Entity, components: MutableList<ComponentClass>): Boolean {
        var hasAllComps = true

        for (comp in components) {
            if (!entity.hasComponent(comp)) {
                hasAllComps = false
                break
            }
        }
        return hasAllComps
    }

    override fun iterator(): Iterator<Entity> {
        return entities.iterator()
    }

    companion object {

        @JvmStatic
        @SafeVarargs
        fun with(vararg components: ComponentClass): Builder {
            return Builder().with(*components)
        }

        @JvmStatic
        @SafeVarargs
        fun without(vararg components: ComponentClass): Builder {
            return Builder().with(*components)
        }

        class Builder {
            private val include = mutableListOf<ComponentClass>()
            private val exclude = mutableListOf<ComponentClass>()

            fun with(vararg components: ComponentClass): Builder {
                for (comp in components)
                    include.add(comp)
                return this
            }

            fun without(vararg components: ComponentClass): Builder {
                for (comp in components)
                    exclude.add(comp)
                return this
            }

            fun get(): Query {
                return Query(include, exclude)
            }
        }
    }
}
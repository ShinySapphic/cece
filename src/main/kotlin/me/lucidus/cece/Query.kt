package me.lucidus.cece

class Query private constructor(private val components: MutableList<ComponentClass>) : Iterable<Entity> {
    private val entities = mutableListOf<Entity>()

    fun contains(entity: Entity): Boolean {
        return entities.contains(entity)
    }

    /**
     * Populates this query from its parameters
     */
    fun populate(engine: Engine) {
        for (ent in engine.getEntities()) {
            if (!checkEntity(ent, components))
                continue
            entities.add(ent)
        }
    }

    /**
     * Checks that this entity contains all this queries components and adds to the list if so.
     */
    internal fun checkAdd(entity: Entity) {
        if (checkEntity(entity, components))
            entities.add(entity)
    }

    private fun checkEntity(entity: Entity, components: MutableList<ComponentClass>): Boolean {
        var hasAllComps = true

        for (comp in components) {
            if (!entity.hasComponent(comp)) {
                hasAllComps = false
                break
            }
        }
        return hasAllComps
    }

    internal fun remove(entity: Entity) {
        entities.remove(entity)
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
                return Query(include)
            }
        }
    }
}
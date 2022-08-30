package me.lucidus.cece

class Query private constructor(private val entities: MutableList<Entity>, private val components: Array<out ComponentClass>) : Iterable<Entity> {

    fun contains(entity: Entity): Boolean {
        return entities.contains(entity)
    }

    /**
     * Checks that this entity contains all this queries components and adds to the list if so.
     */
    internal fun checkAdd(entity: Entity) {
        if (checkEntity(entity, *components))
            entities.add(entity)
    }

    internal fun remove(entity: Entity) {
        entities.remove(entity)
    }

    override fun iterator(): Iterator<Entity> {
        return entities.iterator()
    }

    companion object {

        fun checkEntity(entity: Entity, vararg components: ComponentClass): Boolean {
            var hasAllComps = true

            for (comp in components) {
                if (!entity.hasComponent(comp)) {
                    hasAllComps = false
                    break
                }
            }
            return hasAllComps
        }

        @JvmStatic
        @SafeVarargs
        fun getFor(vararg components: ComponentClass): Query {
            val matches = mutableListOf<Entity>()

            for (ent in Engine.getEntities()) {
                if (!checkEntity(ent, *components))
                    continue
                matches.add(ent)
            }
            return Query(matches, components)
        }
    }
}
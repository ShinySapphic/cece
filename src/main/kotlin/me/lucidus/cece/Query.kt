package me.lucidus.cece

class Query private constructor(private val specifier: QuerySpecifier) : Iterable<EntityRef> {

    @JvmSynthetic
    internal val entities = mutableListOf<EntityRef>()

    fun contains(entity: EntityRef): Boolean {
        for (comp in specifier.exclude) {
            if (entity.hasComponent(comp))
                return false
        }

        for (comp in specifier.include) {
            if (!entity.hasComponent(comp))
                return false
        }
        return true
    }

    override fun iterator(): Iterator<EntityRef> {
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
            return Builder().without(*components)
        }

        class Builder {
            private val queries = HashMap<QuerySpecifier, Query>()

            private val include = mutableSetOf<ComponentClass>()
            private val exclude = mutableSetOf<ComponentClass>()

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
                val specifier = QuerySpecifier(include, exclude)

                if (queries.containsKey(specifier))
                    return queries[specifier]!!
                val query = Query(specifier)
                queries[specifier] = query

                return query
            }
        }
    }
}

class QuerySpecifier(val include: Set<ComponentClass>, val exclude: Set<ComponentClass>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuerySpecifier

        if (include != other.include) return false
        if (exclude != other.exclude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = include.hashCode()
        result = 31 * result + exclude.hashCode()
        return result
    }
}
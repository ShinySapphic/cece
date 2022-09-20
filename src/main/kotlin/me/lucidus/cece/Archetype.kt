package me.lucidus.cece

class Archetype(
    // Holds component ids
    val type: Set<Int> = HashSet(),
    val components: MutableMap<Int, ComponentMap> = HashMap()
) {

    // key = ComponentId
    val edges = HashMap<Int, ArchetypeEdge>()
    val id: Int = count++
    override fun toString(): String {
        return "Archetype{" +
                "type=" + type +
                ", edges=" + edges +
                ", components=" + components +
                ", id=" + id +
                '}'
    }

    companion object {
        private var count = 0
        val EMPTY = Archetype()
    }

}

class ArchetypeEdge(val add: Archetype, val remove: Archetype)

// holds component values with the component id as the key
typealias ComponentMap = MutableMap<Int, Component>
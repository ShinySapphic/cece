package me.lucidus.cece

class Archetype(
    // Holds component ids
    val type: Set<UInt> = HashSet(),
    val components: MutableMap<UInt, ComponentMap> = HashMap()
) {

    // key = ComponentId
    val edges = HashMap<UInt, ArchetypeEdge>()
    val id: UInt = count++
    override fun toString(): String {
        return "Archetype{" +
                "type=" + type +
                ", edges=" + edges +
                ", components=" + components +
                ", id=" + id +
                '}'
    }

    companion object {
        private var count = 0u
        val EMPTY = Archetype()
    }

}

class ArchetypeEdge(val add: Archetype, val remove: Archetype)

// holds component values with the component id as the key
typealias ComponentMap = MutableMap<UInt, Component>
package me.lucidus.cece.archetype

import me.lucidus.cece.Component

class Archetype(
    // Holds component ids
    val type: List<Int> = ArrayList(),
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

// holds component values with the component id as the key
typealias ComponentMap = MutableMap<Int, Component>
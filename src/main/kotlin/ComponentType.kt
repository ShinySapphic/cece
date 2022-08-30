package com.nexus.nexusnpcs.ecs

class ComponentType private constructor() {
    val id: Int = count++

    companion object {
        private val types = HashMap<Class<out Component?>, ComponentType>()
        private var count = 0

        @JvmStatic
        fun getFor(component: Class<out Component>): ComponentType {
            if (!types.containsKey(component))
                types[component] = ComponentType()
            return types[component]!!
        }
    }

}
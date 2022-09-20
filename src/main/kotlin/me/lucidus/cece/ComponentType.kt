package me.lucidus.cece

class ComponentType private constructor() {
    val id: UInt = count++

    companion object {
        private val types = HashMap<Class<out Component?>, ComponentType>()
        private var count = 0u

        @JvmStatic
        fun getFor(component: Class<out Component>): ComponentType {
            if (!types.containsKey(component))
                types[component] = ComponentType()
            return types[component]!!
        }
    }

}
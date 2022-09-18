package me.lucidus.cece

import java.util.logging.Level
import java.util.logging.Logger

class Engine {
    private val logger = Logger.getLogger(Engine::class.java.name)

    private val entityIndex = HashMap<Int, Archetype>()

    // list of archetypes that contain this component id
    private val componentIndex = HashMap<Int, Archetypes>()

    private val systems = mutableListOf<(Pair<Int, AbstractSystem>)>()

    private val entities = mutableListOf<Entity>()

    fun registerSystem(system: AbstractSystem): Engine {
        return registerSystem(0, system)
    }

    fun registerSystem(priority: Int, system: AbstractSystem): Engine {
        system.onAddedToEngine(this)
        val pair = Pair(priority, system)
        systems.add(pair)
        systems.sortBy { it.first }
        return this
    }

    fun update(deltaTime: Float = 0f) {
        for ((_, system) in systems) {
            system.update(deltaTime)
        }
    }

    /**
     * Creates a new entity and adds them to the engine automatically
     */
    fun createEntity(): Entity {
        val ent = Entity(this)
        addEntity(ent)

        return ent
    }

    /**
     * Adds an entity to this engine.
     * @param entity
     *          An object which inherits [Entity]
     */
    fun addEntity(entity: Entity) {
        if (entities.contains(entity)) {
            logger.warning("Entity #${entity.id} has already been added")
            return
        }

        entityIndex[entity.id] = Archetype.EMPTY
        entities.add(entity)

        validateQueries(entity)

        logger.info("Entity #${entity.id} successfully added")
    }

    /**
     * Removes an entity from this engine.
     * @param entity
     *          An object which inherits [Entity]
     */
    fun removeEntity(entity: Entity) {
        if (!entities.contains(entity)) {
            logger.warning("Entity #${entity.id} isn't registered and doesn't need removal")
            return
        }

        val archetype = entityIndex[entity.id] ?: return
        for (compId in archetype.type)
            removeComponent(entity.id, compId)

        validateQueries(entity)

        entities.remove(entity)

        logger.info("Entity #${entity.id} successfully removed")
    }

    fun addComponent(entity: Entity, component: Component) {
        addComponent(entity.id, component)

        validateQueries(entity)
    }

    fun removeComponent(entity: Entity, componentClass: ComponentClass) {
        removeComponent(entity.id, ComponentType.getFor(componentClass ?: return).id)

        validateQueries(entity)
    }

    fun <T : Component?> getComponent(entity: Entity, componentClass: ComponentClass): T? {
        return getComponent(entity.id, ComponentType.getFor(componentClass ?: return null).id)
    }

    fun hasComponent(entity: Entity, componentClass: ComponentClass): Boolean {
        return hasComponent(entity.id, ComponentType.getFor(componentClass ?: return false).id)
    }

    private fun validateQueries(entity: Entity) {
        for ((_, system) in systems) {
            for (query in system.queries)
                query.validate(entity)
        }
    }

    private fun updateIndexes(ent: Int, newArchetype: Archetype) {

        // Update entity archetype
        entityIndex[ent] = newArchetype

        logger.fine("New archetype id is $newArchetype.id")

        // Update component index
        //TODO: Try to update index in O(1) if possible
        for (comp in newArchetype.type) {
            if (componentIndex.containsKey(comp)) {
                val archetypes = componentIndex[comp]

                if (!archetypes!!.contains(newArchetype.id))
                    archetypes.add(newArchetype.id)
            } else
                componentIndex[comp] = mutableSetOf(newArchetype.id)
        }
    }

    private fun removeComponent(ent: Int, componentId: Int) {
        val archetype = entityIndex[ent] ?: return

        logger.fine("Removing component: $componentId from entity: $ent")

        // Find or create new archetype
        val newArchetype: Archetype
        if (!archetype.edges.containsKey(componentId)) {
            val components: List<Int> = archetype.type - componentId
            newArchetype = Archetype(components, archetype.components)

            val edge = ArchetypeEdge(archetype, newArchetype)
            archetype.edges[componentId] = edge
        } else {
            newArchetype = archetype.edges[componentId]!!.remove
        }

        // Remove component object from entity list
        if (archetype.components.containsKey(ent))
            archetype.components[ent]!!.remove(componentId)
        updateIndexes(ent, newArchetype)

        logger.fine("Successfully removed component: $componentId from entity: $ent")
    }

    private fun addComponent(ent: Int, component: Component) {
        val archetype = entityIndex[ent] ?: return
        val componentId = ComponentType.getFor(component.javaClass).id

        logger.fine("Adding component: $componentId to entity: $ent")

        // Find or create new archetype
        val newArchetype: Archetype
        if (!archetype.edges.containsKey(componentId)) {
            val components: List<Int> = archetype.type + componentId
            newArchetype = Archetype(components, archetype.components)

            val edge = ArchetypeEdge(newArchetype, archetype)
            archetype.edges[componentId] = edge
        } else {
            newArchetype = archetype.edges[componentId]!!.add
        }

        // Add component object to entity list
        if (newArchetype.components.containsKey(ent))
            newArchetype.components[ent]!![componentId] = component
        else
            newArchetype.components[ent] = mutableMapOf(Pair(componentId, component))
        updateIndexes(ent, newArchetype)

        logger.fine("Successfully added component: $componentId to entity: $ent")
    }

    @Suppress("unchecked_cast")
    private fun <T : Component?> getComponent(ent: Int, componentId: Int): T? {
        if (!hasComponent(ent, componentId))
            return null

        val archetype = entityIndex[ent]
        return archetype!!.components[ent]!![componentId] as T
    }

    private fun hasComponent(ent: Int, componentId: Int): Boolean {
        val archetype = entityIndex[ent]
        val archetypes = componentIndex[componentId]

        return archetypes?.contains(archetype?.id) ?: false
    }

    fun getEntities(): List<Entity> {
        return entities
    }

    init {
        logger.level = Level.WARNING

//        val handler = ConsoleHandler()
//
//        handler.level = Level.FINER
//        logger.addHandler(handler)
    }
}

typealias Archetypes = MutableSet<Int>
typealias ComponentClass = Class<out Component>?
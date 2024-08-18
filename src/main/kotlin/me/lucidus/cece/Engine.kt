package me.lucidus.cece

import java.lang.IllegalStateException
import java.util.logging.Level
import java.util.logging.Logger

class Engine {
    private val logger = Logger.getLogger(Engine::class.java.name)

    private val entityIndex = HashMap<UInt, Archetype>()

    // list of archetypes that contain this component id
    private val componentIndex = HashMap<UInt, Archetypes>()

    private val queries = HashSet<Query>()

    private val systems = mutableListOf<(Pair<Int, EcsSystem>)>()
    private val entities = mutableListOf<EntityRef>()

    private val modified = mutableListOf<EntityRef>()

    private var isUpdating = false
    private var entityCounter = 0u

    fun registerSystem(system: EcsSystem): Engine {
        return registerSystem(0, system)
    }

    fun registerSystem(priority: Int, system: EcsSystem): Engine {

        // Fetch queries
        for (query in system.queries) {
            if (queries.contains(query))
                continue
            queries.add(query)

            // Populate query
            for (ent in entities) {
                if (query.contains(ent))
                    query.entities.add(ent)
            }
        }

        val pair = Pair(priority, system)
        systems.add(pair)
        systems.sortBy { it.first }
        return this
    }

    fun update(deltaTime: Float = 0f) {
        if (isUpdating)
            throw IllegalStateException("Cannot call update more than once at a time.")
        isUpdating = true

        // Update queries for modified entities
        val iter = modified.iterator()
        while (iter.hasNext()) {
            val ent = iter.next()

            for (query in queries) {
                if (query.contains(ent)) {
                    if (!query.entities.contains(ent))
                        query.entities.add(ent)
                } else
                    query.entities.remove(ent)
            }
            iter.remove()
        }

        for ((_, system) in systems)
            system.update(deltaTime)

        isUpdating = false
    }

    /**
     * Creates a new entity and adds them to the engine automatically
     */
    fun createEntity(): EntityRef {
        val ent = Entity(entityCounter++)
        return addEntity(ent)
    }

    /**
     * Adds an entity to this engine.
     * @param entity
     *          An object which inherits [Entity]
     */
    fun addEntity(entity: Entity) : EntityRef {
        val ref = EntityRef(this, entity.id)
        if (entities.contains(ref)) {
            logger.warning("Entity #${entity.id} has already been added")
            return ref
        }

        entityIndex[ref.id] = Archetype.EMPTY
        validateQuery(ref)

        entities.add(ref)

        logger.info("Entity #${entity.id} successfully added")
        return ref
    }

    /**
     * Retrieves a reference to this entity if added to the engine.
     * Allows you to modify components and remove entity from engine.
     * @param entity
     *          An object which inherits [Entity]
     */
    fun entity(entity: Entity): EntityRef? {

        // This doesn't need to be fast. Systems will fetch entities from populated queries.
        // if this method must be used a lot, it should be cached first
        return entities.find { ref -> entity.id == ref.id }
    }

    private fun validateQuery(entity: EntityRef) {
        if (!modified.contains(entity))
            modified.add(entity)
    }

    private fun updateIndexes(ent: UInt, newArchetype: Archetype) {

        // Update entity archetype
        entityIndex[ent] = newArchetype

        logger.finer("New archetype id is $newArchetype.id")

        // Update component index
        for (comp in newArchetype.type) {
            if (componentIndex.containsKey(comp)) {
                val archetypes = componentIndex[comp]

                if (!archetypes!!.contains(newArchetype.id))
                    archetypes.add(newArchetype.id)
            } else
                componentIndex[comp] = mutableSetOf(newArchetype.id)
        }
    }

    @JvmSynthetic
    internal fun removeEntity(entity: EntityRef) {
        if (!entities.contains(entity)) {
            logger.warning("Attempting to remove an entity that isn't registered.")
            return
        }

        val archetype = entityIndex[entity.id]!!
        for (compId in archetype.type)
            removeComponent(entity, compId)

        validateQuery(entity)

        entities.remove(entity)

        logger.info("Entity #${entity.id} successfully removed")
    }

    @JvmSynthetic
    internal fun removeComponent(entity: EntityRef, componentId: UInt) {
        val archetype = entityIndex[entity.id] ?: return

        logger.fine("Removing component: $componentId from entity: ${entity.id}")

        // Find or create new archetype
        val newArchetype: Archetype
        if (!archetype.edges.containsKey(componentId)) {
            val type: Set<UInt> = archetype.type - componentId
            newArchetype = Archetype(type, archetype.components)

            val edge = ArchetypeEdge(archetype, newArchetype)
            archetype.edges[componentId] = edge
        } else {
            newArchetype = archetype.edges[componentId]!!.remove
        }

        // Remove component object from entity list
        if (archetype.components.containsKey(entity.id))
            archetype.components[entity.id]!!.remove(componentId)
        updateIndexes(entity.id, newArchetype)
        validateQuery(entity)

        logger.fine("Successfully removed component: $componentId from entity: ${entity.id}")
    }

    @JvmSynthetic
    internal fun addComponent(entity: EntityRef, component: Component) {
        val archetype = entityIndex[entity.id] ?: return
        val componentId = ComponentType.getFor(component.javaClass).id

        logger.fine("Adding component: $componentId to entity: ${entity.id}")

        // Find or create new archetype
        val newArchetype: Archetype
        if (!archetype.edges.containsKey(componentId)) {
            val type: Set<UInt> = archetype.type + componentId

            newArchetype = Archetype(type, archetype.components)

            val edge = ArchetypeEdge(newArchetype, archetype)
            archetype.edges[componentId] = edge
        } else {
            newArchetype = archetype.edges[componentId]!!.add
        }

        // Add component object to entity list
        if (newArchetype.components.containsKey(entity.id))
            newArchetype.components[entity.id]!![componentId] = component
        else
            newArchetype.components[entity.id] = mutableMapOf(Pair(componentId, component))
        updateIndexes(entity.id, newArchetype)
        validateQuery(entity)

        logger.fine("Successfully added component: $componentId to entity: ${entity.id}")
    }

    @JvmSynthetic
    @Suppress("unchecked_cast")
    internal fun <T : Component?> getComponent(entity: EntityRef, componentId: UInt): T? {
        if (!hasComponent(entity, componentId))
            return null

        val archetype = entityIndex[entity.id]
        return archetype!!.components[entity.id]!![componentId] as T
    }

    @JvmSynthetic
    internal fun hasComponent(entity: EntityRef, componentId: UInt): Boolean {
        val archetype = entityIndex[entity.id]
        val archetypes = componentIndex[componentId]

        return archetypes?.contains(archetype?.id) ?: false
    }

    init {
        logger.level = Level.INFO

//        val handler = ConsoleHandler()
//
//        handler.level = Level.FINER
//        logger.addHandler(handler)
    }
}

typealias Archetypes = MutableSet<UInt>
typealias ComponentClass = Class<out Component?>

import me.lucidus.cece.Engine
import me.lucidus.cece.Entity
import org.junit.Test
import kotlin.test.*

internal class EntityTest {

    private val engine = Engine()

    @Test
    fun testAddEntities() {
        val entA = engine.createEntity()

        // This should throw a warning in the console
        val entB = Entity(0u)
        engine.addEntity(entB)

        // Isn't this so much nicer than `engine.addEntity(Entity(engine, 1u))` :3
        engine.addEntity(Entity(1u))

        assertEquals(entA, engine.entity(entB))

        // We don't even need to hold a reference to the entity, it's just an id
        assertNotNull(engine.entity(Entity(1u)))
    }

    @Test
    fun testRemoveEntity() {
        val ent = Entity(2u)
        engine.addEntity(ent)

        engine.entity(ent)!!.remove()

        // entity.remove should work instantly
        assertNull(engine.entity(ent))

        engine.update(0f)

        assertNull(engine.entity(ent))
    }

}
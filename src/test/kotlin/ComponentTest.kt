import com.nexus.nexusnpcs.ecs.Component
import com.nexus.nexusnpcs.ecs.Engine
import com.nexus.nexusnpcs.ecs.Entity
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class ComponentTest {

    private val engine = Engine

    @Test
    fun testAddComponent() {
        val entity = addNewEntity()
        val component = HelloComponent("Howdy!")

        entity.addComponent(component)
        assertTrue(entity.hasComponent(component.javaClass))
    }

    @Test
    fun testGetComponent() {
        val entity = addNewEntity()
        val component = HelloComponent("Hello World!")

        entity.addComponent(component)

        val retrieved = entity.getComponent<HelloComponent>(component.javaClass)

        assertNotNull(retrieved)
        println(retrieved.message)
    }

    @Test
    fun testGetMultiple() {
        val entA = addNewEntity()
        val entB = addNewEntity()

        entA.addComponent(HelloComponent("Look, I'm Woody! Howdy howdy howdy."))
        entB.addComponent(HelloComponent("Gimme that"))

        val helloA = entA.getComponent<HelloComponent>(HelloComponent::class.java)
        val helloB = entB.getComponent<HelloComponent>(HelloComponent::class.java)

        assertNotNull(helloA)
        println(helloA.message)

        assertNotNull(helloB)
        println(helloB.message)

        entA.addComponent(FavoriteNumComponent(23))
        entB.addComponent(FavoriteNumComponent(256))

        val numA = entA.getComponent<FavoriteNumComponent>(FavoriteNumComponent::class.java)
        val numB = entB.getComponent<FavoriteNumComponent>(FavoriteNumComponent::class.java)

        assertNotNull(numA)
        println("My favorite number is ${numA.value}!")

        assertNotNull(numB)
        println("Well mine is ${numB.value}!")

    }

    private fun addNewEntity(): Entity {
        val entity = Entity(engine)
        engine.addEntity(entity)
        return entity
    }

    private class HelloComponent(val message: String) : Component
    private class FavoriteNumComponent(val value: Int) : Component
}
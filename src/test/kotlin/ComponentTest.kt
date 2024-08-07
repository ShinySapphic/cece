import me.lucidus.cece.Engine
import me.lucidus.cece.Entity
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class ComponentTest {

    private val engine = Engine()

    @Test
    fun testAddComponent() {
        val e = Entity(0u)
        engine.addEntity(e)

        val entity = engine.entity(e)
        val component = HelloComponent("Howdy!")

        entity!!.addComponent(component)
        assertTrue(entity.hasComponent(component.javaClass))
    }

    @Test
    fun testGetComponent() {
        val entity = engine.entity(engine.createEntity())
        val component = HelloComponent("Hello World!")

        entity!!.addComponent(component)

        val retrieved = entity.getComponent<HelloComponent>(component.javaClass)

        assertNotNull(retrieved)
        println(retrieved.message)
    }

    @Test
    fun testGetMultiple() {
        val entA = engine.entity(engine.createEntity())
        val entB = engine.entity(engine.createEntity())

        entA!!.addComponent(HelloComponent("Look, I'm Woody! Howdy howdy howdy."))
        entB!!.addComponent(HelloComponent("Gimme that"))

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
}
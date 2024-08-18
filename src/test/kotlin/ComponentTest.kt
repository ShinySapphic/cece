import me.lucidus.cece.EcsSystem
import me.lucidus.cece.Engine
import me.lucidus.cece.Entity
import me.lucidus.cece.Query
import org.junit.Test
import kotlin.test.assertFalse
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
        val entity = engine.createEntity()
        val component = HelloComponent("Hello World!")

        entity!!.addComponent(component)

        val retrieved = entity.getComponent(component.javaClass)

        assertNotNull(retrieved)
        println(retrieved.message)
    }

    @Test
    fun testRemoveComponent() {
        val systemA = object : EcsSystem(Query.with(HelloComponent::class.java).get()) {
            override fun update(deltaTime: Float) {
                for (ent in query(0)) {
                    val msg = ent.getComponent(HelloComponent::class.java)!!.message
                    println("ent: ${ent.id} says '${msg}'")
                }
            }
        }
        val systemB = object : EcsSystem(Query.with(HelloComponent::class.java, CoolComponent::class.java).get()) {
            override fun update(deltaTime: Float) {
                for (ent in query(0)) {
                    val msg = ent.getComponent(HelloComponent::class.java)!!.message
                    println("ent: ${ent.id} says '${msg}' I'm sooo cool!!!")
                }
            }
        }
        engine.registerSystem(systemA).registerSystem(systemB)

        val entity = engine.createEntity()
        entity!!.addComponent(HelloComponent("Hiya!")).addComponent(CoolComponent())

        // Run systems before and after removal to make sure queries update
        engine.update(0f)

        entity.removeComponent(CoolComponent::class.java)

        engine.update(0f)

        assertFalse(entity.hasComponent(CoolComponent::class.java))
    }

    @Test
    fun testGetMultiple() {
        val entA = engine.createEntity()
        val entB = engine.createEntity()

        entA!!.addComponent(HelloComponent("Look, I'm Woody! Howdy howdy howdy."))
        entB!!.addComponent(HelloComponent("Gimme that"))

        val helloA = entA.getComponent(HelloComponent::class.java)
        val helloB = entB.getComponent(HelloComponent::class.java)

        assertNotNull(helloA)
        println(helloA.message)

        assertNotNull(helloB)
        println(helloB.message)

        entA.addComponent(FavoriteNumComponent(23))
        entB.addComponent(FavoriteNumComponent(256))

        val numA = entA.getComponent(FavoriteNumComponent::class.java)
        val numB = entB.getComponent(FavoriteNumComponent::class.java)

        assertNotNull(numA)
        println("My favorite number is ${numA.value}!")

        assertNotNull(numB)
        println("Well mine is ${numB.value}!")

    }
}
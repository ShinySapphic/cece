import me.lucidus.cece.AbstractSystem
import me.lucidus.cece.Engine
import me.lucidus.cece.Query
import org.junit.Test

internal class SystemTest {

    private val engine = Engine()

    @Test
    fun runSystems() {
        registerSystems(engine)

        for (i in 0..5) {
            val entA = engine.createEntity()
            entA.addComponent(HelloComponent("Hello World!"))

            val entB = engine.createEntity()
            entB.addComponent(FavoriteNumComponent(5)).addComponent(MultiplierComponent(2))

            val entC = engine.createEntity()
            entC.addComponent(FavoriteNumComponent(25))

            if (i > 2) {
                entA.removeComponent(HelloComponent::class.java)
                entC.addComponent(MultiplierComponent(0))

                engine.createEntity().addComponent(CoolComponent()).addComponent(FavoriteNumComponent(32))
                engine.createEntity().addComponent(LameComponent())
            }
        }

        val maxIter = 3

        // In a real example, you'd call this in your game loop
        for (i in 0..maxIter)
            engine.update()
    }

    private fun registerSystems(engine: Engine) {
        engine.registerSystem(HelloSystem())
        engine.registerSystem(-5, MultiplySystem())
        engine.registerSystem(AddByFiveSystem())

        // With a priority of 5, this should always be called last
        engine.registerSystem(5, PrintNumSystem())

        engine.registerSystem(MultiQuery())
    }

    // Systems
    class HelloSystem : AbstractSystem(Query.with(HelloComponent::class.java).get()) {

        // Only run this system once
        private var isFinished = false

        override fun update(deltaTime: Float) {
            println("HelloSystem: 1")
            if (isFinished)
                return
            for (ent in queries[0]) {
                val hello = ent.getComponent<HelloComponent>(HelloComponent::class.java)
                println("ent: ${ent.id} ${hello!!.message}")
            }
            isFinished = true
        }
    }

    class PrintNumSystem : AbstractSystem(Query.with(FavoriteNumComponent::class.java).get()) {
        override fun update(deltaTime: Float) {
            println("PrintNumSystem: 4 (I should be last)")
            for (ent in queries[0]) {
                val myNum = ent.getComponent<FavoriteNumComponent>(FavoriteNumComponent::class.java)
                println("ent: ${ent.id} My favorite number is: ${myNum!!.value}")
            }
        }
    }

    class MultiplySystem : AbstractSystem(Query.with(FavoriteNumComponent::class.java, MultiplierComponent::class.java).get()) {
        override fun update(deltaTime: Float) {
            println("MultiplySystem: 2 ( I should be first now)")
            for (ent in queries[0]) {
                val myNum = ent.getComponent<FavoriteNumComponent>(FavoriteNumComponent::class.java)
                val multiplier = ent.getComponent<MultiplierComponent>(MultiplierComponent::class.java)
                myNum!!.value *= multiplier!!.value
            }
        }
    }

    class AddByFiveSystem : AbstractSystem(Query.with(FavoriteNumComponent::class.java).without(MultiplierComponent::class.java).get()) {
        override fun update(deltaTime: Float) {
            println("AddByFiveSystem: 3")
            for (ent in queries[0]) {
                val myNum = ent.getComponent<FavoriteNumComponent>(FavoriteNumComponent::class.java)
                myNum!!.value += 5
            }
        }
    }

    class MultiQuery : AbstractSystem(Query.with(CoolComponent::class.java, FavoriteNumComponent::class.java).get(), Query.with(LameComponent::class.java).get()) {
        override fun update(deltaTime: Float) {
            for (entA in queries[0]) {
                val myNum = entA.getComponent<FavoriteNumComponent>(FavoriteNumComponent::class.java)
                println("ent: ${entA.id} I'm cool and have a favorite number: ${myNum!!.value}")

                for (entB in queries[1]) {
                    println("ent: ${entB.id} I'm not as cool as ${entA.id}")
                }
            }
        }
    }
}
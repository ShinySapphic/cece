import me.lucidus.cece.EcsSystem
import me.lucidus.cece.Engine
import me.lucidus.cece.Query
import org.junit.Test

internal class SystemTest {

    private val engine = Engine()

    @Test
    fun runSystems() {
        registerSystems(engine)

        for (i in 0..5) {
            val entA = engine.entity(engine.createEntity())
            entA!!.addComponent(HelloComponent("Hello World!"))

            val entB = engine.entity(engine.createEntity())
            entB!!.addComponent(FavoriteNumComponent(5 * i)).addComponent(MultiplierComponent(2))

            val entC = engine.entity(engine.createEntity())
            entC!!.addComponent(FavoriteNumComponent(25 * i))

            if (i > 2) {
                entA.removeComponent(HelloComponent::class.java)
                entC.addComponent(MultiplierComponent(0))

                engine.entity(engine.createEntity())!!.addComponent(CoolComponent()).addComponent(FavoriteNumComponent(32 * i))
                engine.entity(engine.createEntity())!!.addComponent(LameComponent())
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
    class HelloSystem : EcsSystem(Query.with(HelloComponent::class.java).get()) {

        // Only run this system once
        private var isFinished = false

        override fun update(deltaTime: Float) {
            println("HelloSystem: 1")
            if (isFinished)
                return
            for (ent in query(0)) {
                val hello = ent.getComponent<HelloComponent>(HelloComponent::class.java)
                println("ent: ${ent.id} ${hello!!.message}")
            }
            isFinished = true
        }
    }

    class PrintNumSystem : EcsSystem(Query.with(FavoriteNumComponent::class.java).get()) {
        override fun update(deltaTime: Float) {
            println("PrintNumSystem: 4 (I should be last)")
            for (ent in query(0)) {
                val myNum = ent.getComponent<FavoriteNumComponent>(FavoriteNumComponent::class.java)
                if (myNum!!.value > 150) {
                    println("ent: ${ent.id} is being despawned :o")
                    ent.despawn()
                }
                println("ent: ${ent.id} My favorite number is: ${myNum.value}")
            }
        }
    }

    class MultiplySystem : EcsSystem(Query.with(FavoriteNumComponent::class.java, MultiplierComponent::class.java).get()) {
        override fun update(deltaTime: Float) {
            println("MultiplySystem: 2 ( I should be first now)")
            for (ent in query(0)) {
                val myNum = ent.getComponent<FavoriteNumComponent>(FavoriteNumComponent::class.java)
                val multiplier = ent.getComponent<MultiplierComponent>(MultiplierComponent::class.java)
                myNum!!.value *= multiplier!!.value

                println("Multiplied entity: ${ent.id}'s favorite number. It is now ${myNum!!.value}")

                if (myNum.value >= 100) {
                    println("Removing MultiplierComponent")
                    ent.removeComponent(MultiplierComponent::class.java)
                }
            }
        }
    }

    class AddByFiveSystem : EcsSystem(Query.with(FavoriteNumComponent::class.java).without(MultiplierComponent::class.java).get()) {
        override fun update(deltaTime: Float) {
            println("AddByFiveSystem: 3")
            for (ent in query(0)) {
                val myNum = ent.getComponent<FavoriteNumComponent>(FavoriteNumComponent::class.java)
                myNum!!.value += 5
            }
        }
    }

    class MultiQuery : EcsSystem(Query.with(CoolComponent::class.java, FavoriteNumComponent::class.java).get(), Query.with(LameComponent::class.java).get()) {
        override fun update(deltaTime: Float) {
            for (entA in query(0)) {
                val myNum = entA.getComponent<FavoriteNumComponent>(FavoriteNumComponent::class.java)
                println("ent: ${entA.id} I'm cool and have a favorite number: ${myNum!!.value}")

                for (entB in query(0)) {
                    println("ent: ${entB.id} I'm not as cool as ${entA.id}")
                }
            }
        }
    }
}
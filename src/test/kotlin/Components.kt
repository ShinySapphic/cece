import me.lucidus.cece.Component

internal class HelloComponent(val message: String) : Component
internal class FavoriteNumComponent(var value: Int) : Component
internal class MultiplierComponent(var value: Int) : Component

// These are tag components
internal class CoolComponent : Component
internal class LameComponent : Component
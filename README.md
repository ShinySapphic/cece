# Cece
An ECS library written in Kotlin.

## What is ECS?
ECS stands for Entity Component System. In ECS, entities are nothing more than an ID. They may hold components (data) and systems can implement behavior on entities with a specific set of data. It is used as an alternative to Object Oriented Programming and is extremely useful in projects where you might have a large hierachy of classes. ECS promotes composition over inheritance.

## Why's it called Cece?
Cece is named after my dog who is fairly small, much like this libray.

## Why build this project?
Another project I'm working on required a large amount of entities with different behaviors being chosen by the user. The user may decide to add a healing component or change the value on their health component. Systems may also be reading or writing multiple sets of data, a perk of seperating data and behavior. As a result, using a traditional OOP approach was out of the question.

## What I learned
Through this project, I learned to think more about the data structures I use, how well they perform for certain tasks, and how they're placed in memory.

## Author's note
Cece is not a true ECS implementation. While it does have entities, components, and systems, it does not benefit from data-driven design. Being written in Kotlin, a garbage collected language, there's very little control in how memory is stored. While it won't be nearly as fast compared to ECS's written in C or Rust, it still features one of the most important aspects of ECS, being composition. Cece is better described as using an Entity-Component Pattern.

Though you can certainly use Cece if you want, this project was more of a learning expierence and there are much better ECS implementations out there. I highly recommend checking out [Libgdx's Ashley](https://github.com/libgdx/ashley/wiki) for Java/Kotlin or [Bevy ECS](https://github.com/bevyengine/bevy/tree/main/crates/bevy_ecs) for Rust developers. 

package com.textgame.dungeoncrawl.model.map

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.strategy.GuardStrategy
import com.textgame.engine.model.NamedEntity.Companion.nextId
import com.textgame.engine.model.nounphrase.*
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_NEUTER

class MapGenerator {

    companion object {

        /**
         * Generates a dungeon based on "Bleak Falls Barrow" from Skyrim
         */
        fun generateDungeon(): Map {
            // Player's starting cell
            val antechamber = Location(
                    nextId(),
                    Definite(Adjective("barrow", Noun("antechamber"))),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    "The air is cold and clammy, and bones rattle under your feet."
            )
            antechamber.inventory.add(Item(nextId(), Noun("lock pick")))
            antechamber.inventory.add(Item(nextId(), Adjective("gold", Noun("coin"))))
            generateEnemy(
                    Noun("bandit"),
                    Pronouns.THIRD_PERSON_SINGULAR_MASCULINE,
                    antechamber,
                    listOf(Item(nextId(), Adjective("war", Noun("axe"))))
            )

            // Hallway leading from antechamber
            val hallway = Location(
                    nextId(),
                    Adjective("dark", Noun("hallway")),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    "Your footsteps echo faintly down the long stone corridor."
            )
            generateEnemy(
                    Noun("skeever"),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    hallway
            )
            generateEnemy(
                    Noun("skeleton"),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    hallway,
                    listOf(Item(nextId(), Adjective("bone", Noun("shiv"))))
            )
            connect(antechamber, hallway, CardinalDirection.NORTH)

            // Collapsed storage room
            val collapsedStorage = Location(
                    nextId(),
                    Adjective("collapsed", Adjective("storage", Noun("room"))),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    "Rubble from the caved-in ceiling stands in a heap around the small chamber."
            )
            collapsedStorage.inventory.add(Item(nextId(), Adjective("embalming", Noun("knife"))))
            connect(hallway, collapsedStorage, CardinalDirection.EAST)

            // Room with a puzzle lock (WIP)
            val puzzleLock = Location(
                    nextId(),
                    Adjective("puzzle", Noun("room")),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    "A lever stands on a small stone pedestal in the middle of the floor. Above the metal grate to the north is a carved fresco of a whale. A stone pedestal shows the figure of a snake."
            )
            generateEnemy(
                    Noun("bandit"),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    puzzleLock,
                    listOf(Item(nextId(), Adjective("hunting", Noun("bow"))))
            )
            connect(hallway, puzzleLock, CardinalDirection.NORTH)

            return Map(
                    listOf(antechamber, hallway, puzzleLock),
                    antechamber
            )
        }

        private fun connect(locationA: Location, locationB: Location, direction: CardinalDirection) {
            locationA.doors[direction] = locationB
            locationB.doors[CardinalDirection.opposite(direction)] = locationA
        }

        private fun generateEnemy(name: NounPhrase, pronouns: Pronouns, location: Location, equipment: List<Item> = listOf()) {
            val enemy = Creature(
                    nextId(),
                    name,
                    pronouns,
                    location,
                    GuardStrategy
            )
            equipment.forEach { enemy.inventory.add(it) }

            location.creatures.add(enemy)
        }
    }
}
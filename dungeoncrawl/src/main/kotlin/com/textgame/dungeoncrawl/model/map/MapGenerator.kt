package com.textgame.dungeoncrawl.model.map

import com.textgame.dungeoncrawl.ifPercent
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Consumable
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.item.Weapon
import com.textgame.dungeoncrawl.pick
import com.textgame.dungeoncrawl.strategy.MonsterStrategy
import com.textgame.engine.model.NamedEntity.Companion.nextId
import com.textgame.engine.model.nounphrase.*
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_FEMININE
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_MASCULINE
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_NEUTER
import com.textgame.engine.model.verb.Verb

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
            generateBandit(antechamber)

            // Hallway leading from antechamber
            val hallway = Location(
                    nextId(),
                    Adjective("dark", Noun("hallway")),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    "Your footsteps echo faintly down the long stone corridor."
            )
            generateMonster(
                    Noun("skeever"),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    1,
                    hallway
            )
            generateMonster(
                    Noun("skeleton"),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    20,
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
            connect(hallway, puzzleLock, CardinalDirection.NORTH)
            generateBandit(puzzleLock)

            return Map(
                    listOf(antechamber, hallway, puzzleLock),
                    antechamber
            )
        }

        private fun connect(locationA: Location, locationB: Location, direction: CardinalDirection) {
            locationA.doors[direction] = locationB
            locationB.doors[CardinalDirection.opposite(direction)] = locationA
        }

        private fun generateMonster(name: NounPhrase, pronouns: Pronouns, maxHealth:Int, location: Location, equipment: List<Item> = listOf()) {
            val enemy = Creature(
                    nextId(),
                    name,
                    pronouns,
                    maxHealth,
                    location,
                    MonsterStrategy
            )
            equipment.forEach { enemy.addItem(it) }

            enemy.allyGroups.add("MONSTER")
            location.creatures.add(enemy)
        }

        private fun generateBandit(location: Location) {
            val bandit = Creature(
                    nextId(),
                    Noun("bandit"),
                    pick(THIRD_PERSON_SINGULAR_MASCULINE, THIRD_PERSON_SINGULAR_FEMININE),
                    30,
                    location,
                    MonsterStrategy
            )
            bandit.addItem(humanWeapon())

            ifPercent(75) {
                bandit.addItem(potionItem())
            }

            bandit.allyGroups.add("BANDIT")
            location.creatures.add(bandit)
        }

        fun humanWeapon(): Item =
                pick(
                        { Weapon(nextId(), Adjective("war", Noun("axe"))) },
                        { Weapon(nextId(), Noun("warhammer")) },
                        { Weapon(nextId(), Adjective("iron", Noun("sword"))) },
                        { Weapon(nextId(), Adjective("hunting", Noun("bow"))) }
                )

        fun potionItem() =
                Consumable(
                        nextId(),
                        Adjective("healing", Noun("potion")),
                        THIRD_PERSON_SINGULAR_NEUTER,
                        Verb("drinks", "drink"),
                        10
                )
    }
}
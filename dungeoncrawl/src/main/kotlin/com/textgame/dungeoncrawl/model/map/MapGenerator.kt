package com.textgame.dungeoncrawl.model.map

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.strategy.GuardStrategy
import com.textgame.engine.model.NamedEntity.Companion.nextId
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_NEUTER

class MapGenerator {

    companion object {

        fun generateSmallMap(): Map {
            // Player's starting cell
            val cell = Location(
                    nextId(),
                    Adjective("dank", Noun("cell")),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    "The air is cold and clammy, and bones rattle under your feet."
            )
            cell.inventory.add(Item(nextId(), Adjective("iron", Noun("shackle"))))

            // Hallway adjacent to starting cell
            val hallway = Location(
                    nextId(),
                    Adjective("dark", Noun("hallway")),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    "Your footsteps echo faintly down the long stone corridor."
            )
            generateEnemy(Adjective("prison", Noun("guard")), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE, hallway)
            hallway.inventory.add(Item(nextId(), Adjective("gold", Noun("coin"))))
            hallway.inventory.add(Item(nextId(), Adjective("iron", Noun("coin"))))

            cell.doors[CardinalDirection.NORTH] = hallway
            hallway.doors[CardinalDirection.SOUTH] = cell

            // Another cell off the hallway
            val floodedCell = Location(
                    nextId(),
                    Adjective("flooded", Noun("cell")),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    "Water drips steadily from the ceiling and splashes into the ankle-deep water of this cell."
            )
            generateEnemy(Noun("skeleton"), THIRD_PERSON_SINGULAR_NEUTER, floodedCell)
            floodedCell.doors[CardinalDirection.SOUTH] = hallway
            hallway.doors[CardinalDirection.NORTH] = floodedCell

            return Map(
                    listOf(cell, hallway, floodedCell),
                    cell
            )
        }

        private fun generateEnemy(name: NounPhrase, pronouns: Pronouns, location: Location) {
            val enemy = Creature(
                    nextId(),
                    name,
                    pronouns,
                    location,
                    GuardStrategy
            )
            location.creatures.add(enemy)
        }
    }
}
package com.textgame.dungeoncrawl.model.map

import com.textgame.dungeoncrawl.model.Creature
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.engine.model.NamedEntity.Companion.nextId
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns

class MapGenerator {

    companion object {

        fun generateSmallMap(): Location {
            val cell = Location(
                    nextId(),
                    Adjective("dank", Noun("cell")),
                    Pronouns.THIRD_PERSON_PLURAL_NEUTER,
                    "The air is cold and clammy, and bones rattle under your feet."
            )
            cell.inventory.add(Item(nextId(), Adjective("iron", Noun("shackle"))))

            val hallway = Location(
                    nextId(),
                    Adjective("dark", Noun("hallway")),
                    Pronouns.THIRD_PERSON_PLURAL_NEUTER,
                    "Your footsteps echo faintly down the long stone corridor."
            )
            hallway.inventory.add(Item(nextId(), Adjective("gold", Noun("coin"))))
            hallway.inventory.add(Item(nextId(), Adjective("iron", Noun("coin"))))

            val guard = Creature(nextId(), Adjective("prison", Noun("guard")), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE, hallway)
            hallway.creatures.add(guard)

            cell.doors[CardinalDirection.NORTH] = hallway
            hallway.doors[CardinalDirection.SOUTH] = cell

            return cell
        }
    }
}
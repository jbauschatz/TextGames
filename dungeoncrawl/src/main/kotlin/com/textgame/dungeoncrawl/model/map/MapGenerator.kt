package com.textgame.dungeoncrawl.model.map

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns

class MapGenerator {

    companion object {

        fun generateSmallMap(): Location {
            val cell = Location(
                    Adjective("dank", Noun("cell")),
                    Pronouns.THIRD_PERSON_PLURAL_NEUTER,
                    "The air is cold and clammy, and bones rattle under your feet."
            )
            cell.inventory.addItem(Item(Adjective("iron", Noun("shackle"))))

            val hallway = Location(
                    Adjective("dark", Noun("hallway")),
                    Pronouns.THIRD_PERSON_PLURAL_NEUTER,
                    "Your footsteps echo faintly down the long stone corridor."
            )
            hallway.inventory.addItem(Item(Adjective("gold", Noun("coin"))))

            cell.doors[CardinalDirection.NORTH] = hallway
            hallway.doors[CardinalDirection.SOUTH] = cell

            return cell
        }
    }
}
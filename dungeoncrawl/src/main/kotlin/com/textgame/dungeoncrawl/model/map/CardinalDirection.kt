package com.textgame.dungeoncrawl.model.map

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.ProperNoun
import java.lang.IllegalArgumentException

/**
 * Used to refer to directions within a sentence, ie "You go north"
 */
class CardinalDirection(
        id: Int,
        name: NounPhrase
): NamedEntity(id, name, Pronouns.THIRD_PERSON_SINGULAR_FEMININE) {

    companion object {
        val NORTH = CardinalDirection(nextId(), ProperNoun("north"))
        val SOUTH = CardinalDirection(nextId(), ProperNoun("south"))
        val EAST = CardinalDirection(nextId(), ProperNoun("east"))
        val WEST = CardinalDirection(nextId(), ProperNoun("west"))

        fun opposite(direction: CardinalDirection) = when(direction) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
            else -> throw IllegalArgumentException("Unexpected direction: $direction")
        }
    }
}
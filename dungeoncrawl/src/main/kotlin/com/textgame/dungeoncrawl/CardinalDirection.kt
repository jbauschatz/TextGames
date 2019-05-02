package com.textgame.dungeoncrawl

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.ProperNoun

/**
 * Used to refer to directions within a sentence, ie "You go north"
 */
class CardinalDirection(private val name: NounPhrase): NamedEntity {

    companion object {
        val NORTH = CardinalDirection(ProperNoun("north"))
        val SOUTH = CardinalDirection(ProperNoun("south"))
        val EAST = CardinalDirection(ProperNoun("east"))
        val WEST = CardinalDirection(ProperNoun("west"))
    }

    override fun getName(): NounPhrase =
            name

    override fun getPronouns(): Pronouns =
            Pronouns.THIRD_PERSON_SINGULAR_FEMININE
}
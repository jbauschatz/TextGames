package com.textgame.dungeoncrawl.view

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

/**
 * Read-only representation of a [Creature]'s state at some point in the game.
 */
class CreatureView(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns?,
        val weapon: ItemView?
): NamedEntity(id, name, pronouns) {
    constructor(creature: Creature): this(
            creature.id,
            creature.name,
            creature.pronouns,
            if (creature.weapon != null) ItemView(creature.weapon!!) else null
    )
}
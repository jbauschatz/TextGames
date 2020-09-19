package com.textgame.dungeoncrawl.view

import com.textgame.dungeoncrawl.model.map.CardinalDirection
import com.textgame.dungeoncrawl.model.map.Door
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

/**
 * Read-only representation of a [Door] at a specific moment in the game
 */
class DoorView private constructor(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns?,
        val direction: CardinalDirection
) : NamedEntity(id, name, pronouns) {
    constructor(door: Door) : this(door.id, door.name, door.pronouns, door.direction)
}

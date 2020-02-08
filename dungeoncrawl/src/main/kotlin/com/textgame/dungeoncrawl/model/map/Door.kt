package com.textgame.dungeoncrawl.model.map

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

class Door(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns,
        val direction: CardinalDirection,
        val destination: Location
) : NamedEntity(id, name, pronouns) {
}
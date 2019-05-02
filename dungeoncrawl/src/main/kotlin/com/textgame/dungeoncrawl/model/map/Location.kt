package com.textgame.dungeoncrawl.model.map

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

data class Location(
        override val name: NounPhrase,
        override val pronouns: Pronouns,
        val description: String
): NamedEntity {

    /**
     * Map from directions (ie "north", "up") to the corresponding [Location]
     */
    val doors: MutableMap<NamedEntity, Location> = mutableMapOf()
}
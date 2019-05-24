package com.textgame.dungeoncrawl.view

import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns
import java.util.*

/**
 * Read-only representation of a [Location] at a specific moment in the game
 */
class LocationView private constructor(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns,
        val description: String,
        val items: List<ItemView>,
        val creatures: List<CreatureView>,
        val doors: List<NamedEntity>
): NamedEntity(id, name, pronouns) {

    constructor(location: Location): this(
            location.id,
            location.name,
            location.pronouns,
            location.description,
            location.inventory.members().map { ItemView(it) },
            location.creatures.members().map { CreatureView(it) },
            LinkedList(location.doors.keys)
    )
}
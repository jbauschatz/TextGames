package com.textgame.dungeoncrawl.model.map

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.Inventory
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

class Location(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns,
        val description: String
): NamedEntity(id, name, pronouns) {

    /**
     * Map from directions (ie "north", "up") to the corresponding [Location]
     */
    val doors: MutableMap<NamedEntity, Location> = mutableMapOf()

    /**
     * [Inventory] of [Item]s within the [Location]
     */
    val inventory: Inventory<Item> = Inventory()

    /**
     * [Inventory] of [Creature]s which occupy this [Location]
     */
    val creatures: Inventory<Creature> = Inventory()
}
package com.textgame.dungeoncrawl.model.creature

import com.textgame.dungeoncrawl.model.Inventory
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.strategy.CreatureStrategy
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase

class Creature(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns,
        val maxHealth: Int,
        var location: Location,
        var strategy: CreatureStrategy
): NamedEntity(id, name, pronouns) {

    val inventory: Inventory<Item> = Inventory()

    var weapon: Item? = null

    val actionsAvailable: MutableMap<ActionType, Int> = mutableMapOf()

    var health: Int = maxHealth

    /**
     * Adds the [Item] to the [Inventory] and adds this [NamedEntity] as one of the [Item]'s owners
     */
    fun addItem(item: Item) {
        inventory.add(item)
        item.addOwner(this)
    }
}
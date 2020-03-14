package com.textgame.dungeoncrawl.model.map

import com.textgame.dungeoncrawl.model.Container
import com.textgame.dungeoncrawl.model.Inventory
import com.textgame.dungeoncrawl.model.creature.Creature
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
     * [Door]s which exit this [Location]
     */
    val doors: MutableList<Door> = mutableListOf()

    /**
     * [Inventory] of [Item]s within the [Location]
     */
    val inventory: Inventory<Item> = Inventory()

    /**
     * [Inventory] of [Creature]s which occupy this [Location]
     */
    val creatures: Inventory<Creature> = Inventory()

    /**
     * [Container]s within the [Location]
     */
    val containers: Inventory<Container> = Inventory()

    /**
     * Finds all [Item]s which match the given name, within this [Location]'s [Inventory]
     * or contained in any [Container] within it
     */
    fun findByName(name: String): List<Item> {
        val items = mutableListOf<Item>()

        // Add matches found in the root inventory
        items.addAll(inventory.findByName(name))

        // Add matches found in the containers
        containers.members.forEach {
            items.addAll(it.findByName(name))
        }

        return items
    }

    fun remove(item: Item) {
        // Attempt to remove the item from the top-level inventory
        if (inventory.members.contains(item)) {
            inventory.remove(item)
            return
        }

        // Attempt to remove the item from any container in the location
        containers.members.forEach {
            if (it.contains(item)) {
                it.remove(item)
                return
            }
        }
    }
}
package com.textgame.dungeoncrawl.model

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_NEUTER

/**
 * An game object which exists within a [Location] and contains [Item]s in different named slots.
 *
 * This could be a literal container such as a chest, a piece of furniture for example.
 */
class Container constructor(
        id: Int,
        name: NounPhrase,
        val slotNames: List<String>,
        pronouns: Pronouns = THIRD_PERSON_SINGULAR_NEUTER
): NamedEntity(id, name, pronouns) {

    /**
     * Maps from the slot name to the inventory of items in that slot
     */
    private val slots = slotNames.map{ it to Inventory<Item>() }.toMap()

    /**
     * Gets the Slot with the given name
     */
    fun getSlot(slot: String): Inventory<Item>? =
            slots[slot]

    fun allItems(): List<Item> {
        val allItems = mutableListOf<Item>()
        slots.values.forEach {
            allItems.addAll(it.members)
        }
        return allItems
    }

    /**
     * Finds all [Item]s which match the given name, within all Slots
     */
    fun findByName(name: String): Set<Item> {
        val items = mutableSetOf<Item>()

        slots.values.forEach {
            items.addAll(it.findByName(name))
        }

        return items
    }

    fun contains(item: Item) =
            slots.values.any { it.members.contains(item) }

    /**
     * Removes the [Item] from whichever Slot it occupies within the [Container]
     */
    fun remove(item: Item) {
        slots.values.forEach {
            if (it.members.contains(item)) {
                it.remove(item)
                return
            }
        }
    }
}
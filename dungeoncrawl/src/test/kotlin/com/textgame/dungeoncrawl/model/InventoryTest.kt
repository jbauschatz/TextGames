package com.textgame.dungeoncrawl.model

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.engine.model.nounphrase.Noun
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

class InventoryTest {

    @Test
    fun items_containsItems() {
        // GIVEN an Inventory to which multiple items are added
        val inventory = Inventory()

        val item1 = Item(Noun("item1"))
        inventory.addItem(item1)

        val item2 = Item(Noun("item2"))
        inventory.addItem(item2)

        val item3 = Item(Noun("item3"))
        inventory.addItem(item3)

        // WHEN accessing the inventory's items
        val items = inventory.items()

        // EXPECT all items to be present
        assertThat(items, containsInAnyOrder(item1, item2, item3))
    }

    @Test
    fun findById_none() {
        // GIVEN an Inventory containing multiple items
        val inventory = Inventory()
        inventory.addItem(Item(Noun("item1")))
        inventory.addItem(Item(Noun("item2")))

        // WHEN searching by a name that is not present
        val items = inventory.findByName("item3")

        // EXPECT no items to be found
        assertThat(items, empty())
    }

    @Test
    fun findById_oneExactMatch() {
        // GIVEN an Inventory containing multiple items
        val inventory = Inventory()

        val item1 = Item(Noun("item1"))
        inventory.addItem(item1)

        val item2 = Item(Noun("item2"))
        inventory.addItem(item2)

        // WHEN searching by the exact name of an item
        val items = inventory.findByName("item1")

        // EXPECT the item to be found
        assertThat(items, contains(item1))
    }

    @Test
    fun findById_oneMatchIgnoreCase() {
        // GIVEN an Inventory containing multiple items
        val inventory = Inventory()

        val item1 = Item(Noun("item1"))
        inventory.addItem(item1)

        inventory.addItem(Item(Noun("item2")))

        // WHEN searching by the the name of an item, differently capitalized
        val items = inventory.findByName("ITEM1")

        // EXPECT the item to be found
        assertThat(items, contains(item1))
    }

    @Test
    fun findById_multipleMatches() {
        // GIVEN an Inventory containing multiple items
        val inventory = Inventory()

        val item1 = Item(Noun("key"))
        inventory.addItem(item1)

        inventory.addItem(Item(Noun("spoon")))

        val item2 = Item(Noun("key"))
        inventory.addItem(item2)

        // WHEN searching by the the name of multiple items
        val items = inventory.findByName("KEY")

        // EXPECT each matching item to be found
        assertThat(items, containsInAnyOrder(item1, item2))
    }
}
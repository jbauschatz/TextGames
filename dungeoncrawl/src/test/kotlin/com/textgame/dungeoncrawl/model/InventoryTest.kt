package com.textgame.dungeoncrawl.model

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

class InventoryTest {

    @Test
    fun members_containsItems() {
        // GIVEN an Inventory to which multiple items are added
        val inventory = Inventory<Item>()

        val item1 = Item(1, Noun("item1"))
        inventory.add(item1)

        val item2 = Item(2, Noun("item2"))
        inventory.add(item2)

        val item3 = Item(3, Noun("item3"))
        inventory.add(item3)

        // WHEN accessing the inventory's items
        val items = inventory.members()

        // EXPECT all items to be present
        assertThat(items, containsInAnyOrder(item1, item2, item3))
    }

    @Test
    fun findById_none() {
        // GIVEN an Inventory containing multiple items
        val inventory = Inventory<Item>()
        inventory.add(Item(1, Noun("item1")))
        inventory.add(Item(2, Noun("item2")))

        // WHEN searching by a name that is not present
        val items = inventory.findByName("item3")

        // EXPECT no items to be found
        assertThat(items, empty())
    }

    @Test
    fun findById_oneExactMatch() {
        // GIVEN an Inventory containing multiple items
        val inventory = Inventory<Item>()

        val item1 = Item(1, Noun("item1"))
        inventory.add(item1)

        val item2 = Item(2, Noun("item2"))
        inventory.add(item2)

        // WHEN searching by the exact name of an item
        val items = inventory.findByName("item1")

        // EXPECT the item to be found
        assertThat(items, contains(item1))
    }

    @Test
    fun findById_oneMatchIgnoreCase() {
        // GIVEN an Inventory containing multiple items
        val inventory = Inventory<Item>()

        val item1 = Item(1, Noun("item1"))
        inventory.add(item1)

        inventory.add(Item(2, Noun("item2")))

        // WHEN searching by the name of an item, differently capitalized
        val items = inventory.findByName("ITEM1")

        // EXPECT the item to be found
        assertThat(items, contains(item1))
    }

    @Test
    fun findById_multipleMatches() {
        // GIVEN an Inventory containing multiple items
        val inventory = Inventory<Item>()

        val item1 = Item(1, Noun("key"))
        inventory.add(item1)

        inventory.add(Item(1, Noun("spoon")))

        val item2 = Item(1, Noun("key"))
        inventory.add(item2)

        // WHEN searching by the name of multiple items
        val items = inventory.findByName("KEY")

        // EXPECT each matching item to be found
        assertThat(items, containsInAnyOrder(item1, item2))
    }

    @Test
    fun findByName_distinctPartialMatch() {
        // GIVEN an Inventory containing multiple items
        val inventory = Inventory<Item>()

        val item = Item(1, Adjective("gold", Noun("coin")))
        inventory.add(item)
        inventory.add(Item(1, Noun("spoon")))

        // WHEN searching by the partial name of one of the items
        val items = inventory.findByName("coin")

        // EXPECT the item to be found
        assertThat(items, containsInAnyOrder(item))
    }
}
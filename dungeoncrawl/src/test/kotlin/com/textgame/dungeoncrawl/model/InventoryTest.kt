package com.textgame.dungeoncrawl.model

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.engine.model.nounphrase.Noun
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
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
        val items = inventory.members

        // EXPECT all items to be present
        assertThat(items, containsInAnyOrder(item1, item2, item3))
    }

}
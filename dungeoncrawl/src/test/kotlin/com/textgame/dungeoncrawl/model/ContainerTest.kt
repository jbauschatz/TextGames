package com.textgame.dungeoncrawl.model

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.engine.model.NamedEntity.Companion.nextId
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

class ContainerTest {

    @Test
    fun getSlot_valid() {
        // GIVEN a Container with a slot called "on"
        val container = Container(1, Noun("table"), listOf("on"))

        // WHEN getting the Container's "on" slot
        val inventory = container.getSlot("on")

        // EXPECT the inventory to exist
        assertThat(inventory, not(nullValue()))
    }

    @Test
    fun getSlot_invalid() {
        // GIVEN a Container with no slot called "on"
        val container = Container(1, Noun("table"), listOf("under"))

        // WHEN getting the Container's "on" slot
        val inventory = container.getSlot("on")

        // EXPECT the inventory not to exist
        assertThat(inventory, nullValue())
    }

    @Test
    fun findByName_multipleSlots() {
        // GIVEN a Container with items named "coin" in multiple slots
        val container = Container(1, Noun("table"), listOf("on", "under"))

        val item1 = Item(nextId(), Adjective("copper", Noun("coin")))
        container.getSlot("on")!!.add(item1)
        container.getSlot("on")!!.add(Item(nextId(), Noun("candle")))

        val item2 = Item(nextId(), Noun("coin"))
        container.getSlot("under")!!.add(item2)

        // WHEN getting items named "coin"
        val items = container.findByName("coin")

        // EXPECT both items
        assertThat(items, containsInAnyOrder(item1, item2))
    }

}
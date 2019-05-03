package com.textgame.dungeoncrawl.model

import com.textgame.dungeoncrawl.model.item.Item

class Inventory {

    private val items: MutableList<Item> = mutableListOf()

    fun addItem(item: Item) =
            items.add(item)

    fun removeItem(item: Item) =
            items.remove(item)

    fun items(): List<Item> =
            items
}
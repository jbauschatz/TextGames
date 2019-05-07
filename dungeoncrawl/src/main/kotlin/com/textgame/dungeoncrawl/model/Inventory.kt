package com.textgame.dungeoncrawl.model

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.engine.model.nounphrase.NounPhraseFormatter

class Inventory {

    private val items: MutableList<Item> = mutableListOf()

    fun addItem(item: Item) =
            items.add(item)

    fun removeItem(item: Item) =
            items.remove(item)

    fun items(): List<Item> =
            items

    fun findByName(name: String): List<Item> =
            items.filter { NounPhraseFormatter.format(it.name).toLowerCase() == name.toLowerCase() }
}
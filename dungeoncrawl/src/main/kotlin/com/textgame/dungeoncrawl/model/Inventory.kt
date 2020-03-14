package com.textgame.dungeoncrawl.model

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.PartialNameMatcher

class Inventory<T : NamedEntity> {

    private val _members: MutableList<T> = mutableListOf()

    val members get() = _members

    fun add(item: T) =
            _members.add(item)

    fun remove(item: T) =
            _members.remove(item)

    /**
     * Finds all [Item]s which match the given name
     */
    fun findByName(name: String): List<T> =
            _members.filter { PartialNameMatcher.matches(name, it.name) }

}
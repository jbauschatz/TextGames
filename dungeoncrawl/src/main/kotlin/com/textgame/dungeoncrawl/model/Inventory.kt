package com.textgame.dungeoncrawl.model

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.PartialNameMatcher

class Inventory<T : NamedEntity> {

    private val members: MutableList<T> = mutableListOf()

    fun add(item: T) =
            members.add(item)

    fun remove(item: T) =
            members.remove(item)

    fun members(): List<T> =
            members

}
package com.textgame.dungeoncrawl.model

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase

class Creature(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns,
        var location: Location
): NamedEntity(id, name, pronouns) {

    val inventory: Inventory<Item> = Inventory()

    var weapon: Item? = null
}
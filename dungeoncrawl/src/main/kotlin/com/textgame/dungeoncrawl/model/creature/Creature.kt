package com.textgame.dungeoncrawl.model.creature

import com.textgame.dungeoncrawl.model.Inventory
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.strategy.CreatureStrategy
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase

class Creature(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns,
        var location: Location,
        var strategy: CreatureStrategy
): NamedEntity(id, name, pronouns) {

    val inventory: Inventory<Item> = Inventory()

    var weapon: Item? = null
}
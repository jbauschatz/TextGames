package com.textgame.dungeoncrawl.view

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

/**
 * Read-only representation of an [Item]'s state at some point in the game.
 */
class ItemView(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns?
): NamedEntity(id, name, pronouns) {
    constructor(item: Item): this(item.id, item.name, item.pronouns) {
        // TODO in the interest of information hiding, this should not be the full instance
        item.owners.forEach { addOwner(it) }
    }
}
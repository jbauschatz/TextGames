package com.textgame.dungeoncrawl.view

import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.item.Weapon
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.verb.Verb

/**
 * Read-only representation of an [Item]'s state at some point in the game.
 */
class WeaponView(
        id: Int,
        name: NounPhrase,
        val attackVerbs: List<Verb>,
        pronouns: Pronouns?
): ItemView(id, name, pronouns) {
    constructor(weapon: Weapon): this(weapon.id, weapon.name, weapon.attackVerbs, weapon.pronouns) {
        // TODO in the interest of information hiding, this should not be the full instance
        weapon.owners.forEach { addOwner(it) }
    }
}
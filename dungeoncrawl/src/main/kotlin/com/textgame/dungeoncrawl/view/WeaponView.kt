package com.textgame.dungeoncrawl.view

import com.textgame.dungeoncrawl.english.AttackSentenceSupplier
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.item.Weapon
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

/**
 * Read-only representation of an [Item]'s state at some point in the game.
 */
class WeaponView(
        id: Int,
        name: NounPhrase,
        val attackSentenceSupplier: List<AttackSentenceSupplier>,
        val attackMissSentenceSupplier: List<AttackSentenceSupplier>,
        pronouns: Pronouns?
): ItemView(id, name, pronouns) {
    constructor(weapon: Weapon): this(
            weapon.id,
            weapon.name,
            weapon.attackSentenceSuppliers,
            weapon.attackMissSentenceSuppliers,
            weapon.pronouns
    ) {
        // TODO in the interest of information hiding, this should not be the full instance
        weapon.owners.forEach { addOwner(it) }
    }
}
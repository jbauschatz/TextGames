package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Weapon
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.WeaponView

/**
 * [GameEvent] which represents a [Creature] equipping an [Weapon]
 */
class EquipWeaponEvent(
        val actor: CreatureView,
        val weapon: WeaponView
): GameEvent {

    constructor(actor: Creature, weapon: Weapon): this(
            CreatureView(actor),
            WeaponView(weapon)
    )
}
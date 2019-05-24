package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.view.CreatureView

/**
 * [GameEvent] which indicates a [Creature] checked their inventory
 */
class InventoryEvent(
        val actor: CreatureView
): GameEvent {

    constructor(actor: Creature): this(CreatureView(actor))
}
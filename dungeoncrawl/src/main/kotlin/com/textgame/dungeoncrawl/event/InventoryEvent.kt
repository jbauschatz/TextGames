package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.Creature

/**
 * [GameEvent] which indicates a [Creature] checked their inventory
 */
class InventoryEvent(
        val actor: Creature
): GameEvent
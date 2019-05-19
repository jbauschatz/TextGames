package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location

/**
 * [GameEvent] which indicates a [Creature] observed their [Location]
 */
class LookEvent(
        val actor: Creature,
        val location: Location
): GameEvent
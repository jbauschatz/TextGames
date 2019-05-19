package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.engine.model.NamedEntity

/**
 * [GameEvent] which indicates a [Creature] moved from one [Location] to another.
 */
class MoveEvent(
        val actor: Creature,
        val direction: NamedEntity,
        val fromLocation: Location,
        val toLocation: Location
): GameEvent
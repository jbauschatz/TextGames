package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.Creature
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location

/**
 * [GameEvent] which indicates a [Creature] took an [Item] from a [Location]
 */
class TakeItemEvent(
        val actor: Creature,
        val item: Item,
        val location: Location
): GameEvent
package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.ItemView
import com.textgame.dungeoncrawl.view.LocationView

/**
 * [GameEvent] which indicates a [Creature] took an [Item] from a [Location]
 */
class TakeItemEvent(
        val actor: CreatureView,
        val item: ItemView,
        val location: LocationView
): GameEvent {

    constructor(actor: Creature, item: Item, location: Location): this(
            CreatureView(actor),
            ItemView(item),
            LocationView(location)
    )
}
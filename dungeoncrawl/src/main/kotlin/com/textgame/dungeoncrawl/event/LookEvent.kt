package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.LocationView

/**
 * [GameEvent] which indicates a [Creature] observed their [Location]
 */
class LookEvent(
        val actor: CreatureView,
        val location: LocationView
): GameEvent {

    constructor(actor: Creature, location: Location): this(
            CreatureView(actor),
            LocationView(location)
    )
}
package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.LocationView
import com.textgame.engine.model.NamedEntity

/**
 * [GameEvent] which indicates a [Creature] moved from one [Location] to another.
 */
class MoveEvent(
        val actor: CreatureView,
        val direction: NamedEntity,
        val fromLocation: LocationView,
        val toLocation: LocationView
): GameEvent {

    constructor(actor: Creature, direction: NamedEntity, fromLocation: Location, toLocation: Location): this(
            CreatureView(actor),
            direction,
            LocationView(fromLocation),
            LocationView(toLocation)
    )
}
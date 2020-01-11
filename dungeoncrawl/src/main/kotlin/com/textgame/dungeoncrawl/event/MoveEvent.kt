package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.CardinalDirection
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.LocationView

/**
 * [GameEvent] which indicates a [Creature] moved from one [Location] to another.
 */
class MoveEvent(
        val actor: CreatureView,
        val direction: CardinalDirection,
        val fromLocation: LocationView,
        val toLocation: LocationView
): GameEvent {

    constructor(actor: Creature, direction: CardinalDirection, fromLocation: Location, toLocation: Location): this(
            CreatureView(actor),
            direction,
            LocationView(fromLocation),
            LocationView(toLocation)
    )
}
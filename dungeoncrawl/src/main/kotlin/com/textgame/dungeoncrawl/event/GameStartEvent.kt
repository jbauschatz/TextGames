package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.view.LocationView

/**
 * [GameEvent] indicating that the game has been initialized and its state can be displayed
 */
class GameStartEvent(
        val startingLocation: LocationView
): GameEvent
package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.CardinalDirection

class MoveCommand(
        val actor: Creature,
        val direction: CardinalDirection
) : GameCommand

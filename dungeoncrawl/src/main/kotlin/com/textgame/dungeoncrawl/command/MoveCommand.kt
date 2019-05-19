package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.engine.model.NamedEntity

class MoveCommand(
        val actor: Creature,
        val direction: NamedEntity
) : GameCommand

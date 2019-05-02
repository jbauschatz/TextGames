package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.Creature
import com.textgame.engine.model.NamedEntity

class MoveCommand(
        val mover: Creature,
        val direction: NamedEntity
) : GameCommand

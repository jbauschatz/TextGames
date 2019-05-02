package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.Creature
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase

class MoveCommand(
        val mover: Creature,
        val direction: NamedEntity
) : GameCommand

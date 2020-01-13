package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.model.creature.Creature

interface CreatureStrategy {

    fun act(creature: Creature): GameCommand?

}
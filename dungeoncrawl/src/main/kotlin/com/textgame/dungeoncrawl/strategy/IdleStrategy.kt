package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.WaitCommand
import com.textgame.dungeoncrawl.model.creature.Creature

object IdleStrategy: CreatureStrategy {

    override fun act(creature: Creature): GameCommand =
            WaitCommand(creature)

}
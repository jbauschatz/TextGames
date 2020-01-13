package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.WaitCommand
import com.textgame.dungeoncrawl.model.creature.ActionType
import com.textgame.dungeoncrawl.model.creature.Creature
import hasActionAvailable

class PriorityStrategy(
        private val strategies: List<CreatureStrategy>
): CreatureStrategy {

    override fun act(creature: Creature): GameCommand? {
        if (!creature.hasActionAvailable(ActionType.ATTACK))
            return WaitCommand(creature)

        strategies.forEach {
            val command = it.act(creature)
            if (command != null)
                return command
        }

        return WaitCommand(creature)
    }

}
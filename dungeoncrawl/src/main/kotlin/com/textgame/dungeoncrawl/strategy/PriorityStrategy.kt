package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.WaitCommand
import com.textgame.dungeoncrawl.model.creature.Creature

class PriorityStrategy(
        private val strategies: List<CreatureStrategy>
): CreatureStrategy {

    override fun act(creature: Creature): GameCommand? {
        strategies.forEach {
            val command = it.act(creature)
            if (command != null)
                return command
        }

        return WaitCommand(creature)
    }

}
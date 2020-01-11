package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.WaitCommand
import com.textgame.dungeoncrawl.model.creature.ActionType
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location
import hasActionAvailable

/**
 * [CreatureStrategy] for a [Creature] which waits in a single [Location] until an enemy arrives.
 */
object GuardStrategy: CreatureStrategy {
    override fun act(creature: Creature): GameCommand {
        if (!creature.hasActionAvailable(ActionType.ATTACK))
            return WaitCommand(creature)

        return creature.attackAnEnemy()
    }
}
package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.MoveCommand
import com.textgame.dungeoncrawl.command.WaitCommand
import com.textgame.dungeoncrawl.model.creature.ActionType
import com.textgame.dungeoncrawl.model.creature.Creature
import hasActionAvailable

class CompanionStrategy(val leader: Creature): CreatureStrategy {

    override fun act(creature: Creature): GameCommand {
        if (!creature.hasActionAvailable(ActionType.ATTACK))
            return WaitCommand(creature)

        // Attack an enemy in the Leader's Location
        if (creature.location.equals(leader.location))
            return creature.attackAnEnemy()

        // Attempt to find a path to the Leader
        val leaderLocation = leader.location
        val doors = creature.location.doors
        doors.keys.forEach {
            if (doors[it] == leaderLocation)
                return MoveCommand(creature, it)
        }

        return WaitCommand(creature)
    }

}
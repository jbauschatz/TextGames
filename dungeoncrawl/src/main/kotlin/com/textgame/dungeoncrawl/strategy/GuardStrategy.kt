package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.AttackCommand
import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.WaitCommand
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location
import enemies

/**
 * [CreatureStrategy] for a [Creature] which waits in a single [Location] until an enemy arrives.
 */
object GuardStrategy: CreatureStrategy {
    override fun act(creature: Creature): GameCommand {
        val enemies = creature.enemies()

        if (enemies.isEmpty()) {
            return WaitCommand(creature)
        }

        // Pick an arbitrary enemy to attack
        val target = enemies[0]

        if (creature.weapon == null) {
            // TODO identify a weapon in inventory and equip it

            // Execute an unarmed attack
            return AttackCommand(creature, target, null)
        }

        return AttackCommand(creature, target, creature.weapon)
    }
}
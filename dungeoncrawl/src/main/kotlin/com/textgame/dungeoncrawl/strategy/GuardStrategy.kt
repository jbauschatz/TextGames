package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.AttackCommand
import com.textgame.dungeoncrawl.command.EquipItemCommand
import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.WaitCommand
import com.textgame.dungeoncrawl.model.creature.ActionType
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location
import enemies
import hasActionAvailable

/**
 * [CreatureStrategy] for a [Creature] which waits in a single [Location] until an enemy arrives.
 */
object GuardStrategy: CreatureStrategy {
    override fun act(creature: Creature): GameCommand {
        if (!creature.hasActionAvailable(ActionType.ATTACK))
            return WaitCommand(creature)

        val enemies = creature.enemies()

        if (enemies.isEmpty())
            return WaitCommand(creature)

        // Pick an arbitrary enemy to attack
        val target = enemies[0]

        if (creature.weapon == null) {
            if (creature.inventory.members().isNotEmpty()) {
                // Pick an arbitrary item to equip
                val weapon = creature.inventory.members()[0]
                return EquipItemCommand(creature, weapon)
            } else {
                // Execute an unarmed attack
                return AttackCommand(creature, target, null)
            }
        }

        // Attack the target with the equipped weapon
        return AttackCommand(creature, target, creature.weapon)
    }
}
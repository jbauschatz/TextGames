package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.AttackCommand
import com.textgame.dungeoncrawl.command.EquipItemCommand
import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.MoveCommand
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location
import enemies

fun companionStrategy(leader: Creature) =
        PriorityStrategy(listOf(
                AttackNearbyEnemy,
                FollowCreature(leader)
        ))

val MonsterStrategy = PriorityStrategy(listOf(
        AttackNearbyEnemy
))

object AttackNearbyEnemy: CreatureStrategy {

    override fun act(creature: Creature): GameCommand? =
            creature.attackAnEnemy()

}

class FollowCreature(private val leader: Creature) : CreatureStrategy {

    override fun act(creature: Creature): GameCommand? {
        // Attempt to find a path to the Leader
        val leaderLocation = leader.location
        val doors = creature.location.doors
        doors.keys.forEach {
            if (doors[it] == leaderLocation)
                return MoveCommand(creature, it)
        }

        return null
    }

}

/**
 * Attempt to attack the given [Creature]
 */
fun Creature.attack(target: Creature): GameCommand {
    // Attack the target with the equipped weapon
    if (this.weapon != null)
        return AttackCommand(this, target, this.weapon)

    // Pick an arbitrary item to equip
    if (this.inventory.members().isNotEmpty()) {
        val weapon = this.inventory.members()[0]
        return EquipItemCommand(this, weapon)
    }

    // Execute an unarmed attack
    return AttackCommand(this, target, null)
}

/**
 * Attempts to attack one of the [Creature]'s enemies in its [Location]
 */
fun Creature.attackAnEnemy(): GameCommand? {
    val enemies = this.enemies()

    if (enemies.isEmpty())
        return null

    // Pick an arbitrary enemy to attack
    val target = enemies[0]
    return this.attack(target)
}
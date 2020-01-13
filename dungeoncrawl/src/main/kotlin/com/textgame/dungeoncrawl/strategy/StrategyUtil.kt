package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.*
import com.textgame.dungeoncrawl.model.creature.ActionType
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location
import enemies
import hasActionAvailable

fun companionStrategy(leader: Creature) =
        PriorityStrategy(listOf(
                EquipWeaponIfThreatened,
                AttackNearbyEnemy,
                FollowCreature(leader),
                UnequipWeaponIfSafe
        ))

val MonsterStrategy = PriorityStrategy(listOf(
        EquipWeaponIfThreatened,
        AttackNearbyEnemy,
        UnequipWeaponIfSafe
))

object EquipWeaponIfThreatened: CreatureStrategy {

    override fun act(creature: Creature): GameCommand? {
        val enemies = creature.enemies()

        if (enemies.isEmpty() || creature.weapon != null)
            return null

        // Pick an arbitrary item to equip
        if (creature.inventory.members().isNotEmpty()) {
            val weapon = creature.inventory.members()[0]
            return EquipItemCommand(creature, weapon)
        }

        return null
    }

}

object AttackNearbyEnemy: CreatureStrategy {

    override fun act(creature: Creature): GameCommand? =
            creature.attackAnEnemy()

}

class FollowCreature(private val leader: Creature) : CreatureStrategy {

    override fun act(creature: Creature): GameCommand? {
        if (!creature.hasActionAvailable(ActionType.MOVE))
            return null

        val leaderLocation = leader.location
        if (leaderLocation == creature.location)
            return null

        // Attempt to find a path to the Leader
        val doors = creature.location.doors
        doors.keys.forEach {
            if (doors[it] == leaderLocation)
                return MoveCommand(creature, it)
        }

        return null
    }

}

object UnequipWeaponIfSafe: CreatureStrategy {

    override fun act(creature: Creature): GameCommand? {
        val enemies = creature.enemies()

        if (enemies.isNotEmpty())
            return null

        if (creature.weapon == null)
            return null

        return UnequipItemCommand(creature, creature.weapon!!)
    }

}

/**
 * Attack the given [Creature] with a weapon if equipped, or make an unarmed strike
 */
fun Creature.attack(target: Creature): GameCommand? {
    if (!this.hasActionAvailable(ActionType.ATTACK))
        return null

    // Attack the target with the equipped weapon
    if (this.weapon != null)
        return AttackCommand(this, target, this.weapon)

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
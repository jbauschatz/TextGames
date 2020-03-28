package com.textgame.dungeoncrawl.strategy

import com.textgame.dungeoncrawl.command.*
import com.textgame.dungeoncrawl.model.creature.ActionType
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.*
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.pick
import enemies
import hasActionAvailable

fun companionStrategy(leader: Creature) =
        PriorityStrategy(listOf(
                EquipWeaponIfThreatened,
                AttackNearbyEnemy,
                FollowCreature(leader),
                UnequipWeaponIfSafe,
                ConsumeHealingItemIfInjured
        ))

val AdventurerStrategy = PriorityStrategy(listOf(
        EquipWeaponIfThreatened,
        ConsumeHealingItemIfInjured,
        AttackNearbyEnemy,
        UnequipWeaponIfSafe,
        LootRoom,
        ExploreRandomly
))

val MonsterStrategy = PriorityStrategy(listOf(
        EquipWeaponIfThreatened,
        ConsumeHealingItemIfInjured,
        AttackNearbyEnemy,
        UnequipWeaponIfSafe
))

object EquipWeaponIfThreatened: CreatureStrategy {

    override fun act(creature: Creature): GameCommand? {
        val enemies = creature.enemies()

        if (enemies.isEmpty() || creature.weapon != null)
            return null

        // Pick an arbitrary item to equip
        creature.inventory.members.forEach {
            if (it is Weapon)
                return EquipItemCommand(creature, it)
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
        creature.location.doors.forEach {
            if (it.destination == leaderLocation)
                return MoveCommand(creature, it.direction)
        }

        return null
    }

}

object LootRoom: CreatureStrategy {
    override fun act(creature: Creature): GameCommand? {
        val items = creature.location.allItems().filter {
            it is Weapon
                    || it is Consumable
                    || it is Treasure
        }

        if (items.isEmpty())
            return null

        val item = pick(items)
        return TakeItemCommand(creature, item, creature.location)
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

object ConsumeHealingItemIfInjured: CreatureStrategy {

    override fun act(creature: Creature): GameCommand? {
        if (!creature.hasActionAvailable(ActionType.ATTACK))
            return null

        val healthPercentage = creature.health * 100 / creature.maxHealth

        if (healthPercentage > 50)
            return null

        creature.inventory.members.forEach {
            if (it is Consumable) {
                return UseItemCommand(creature, it)
            }
        }

        return null
    }

}

object ExploreRandomly: CreatureStrategy {
    override fun act(creature: Creature): GameCommand? {
        if (!creature.hasActionAvailable(ActionType.MOVE))
            return null

        val door = pick(creature.location.doors)

        return MoveCommand(creature, door.direction)
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
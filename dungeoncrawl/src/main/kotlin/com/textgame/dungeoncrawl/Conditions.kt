package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.model.creature.Creature
import isDead

interface GameCondition {

    fun isTriggered(): Boolean
}

/**
 * [GameCondition] which triggers when all the [Creature]s have died
 */
class DeathCondition(private val creatures: List<Creature>): GameCondition {

    override fun isTriggered() = creatures.all { it.isDead() }

}
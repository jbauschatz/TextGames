package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.model.creature.Creature
import isDead

interface GameCondition {

    fun isTriggered(): Boolean
}

/**
 * [GameCondition] which triggers up the given [Creature]'s death
 */
class DeathCondition(private val creature: Creature): GameCondition {

    override fun isTriggered() = creature.isDead()

}
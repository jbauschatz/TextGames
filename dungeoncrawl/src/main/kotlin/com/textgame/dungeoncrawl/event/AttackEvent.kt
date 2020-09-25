package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Weapon
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.WeaponView

/**
 * [GameEvent] indicating one [Creature] attacked another.
 */
class AttackEvent(

        /**
         * [Creature] making the attack
         */
        val attacker: CreatureView,

        /**
         * [Creature] receiving the attack
         */
        val defender: CreatureView,

        /**
         * Whether the attack hits the target
         */
        val hits: Boolean,

        /**
         * Whether [defender] was killed by this attack
         */
        val isLethal: Boolean,

        /**
         * [Weapon] used to attack with
         *
         * If null, indicates an unarmed attack
         */
        val weapon: WeaponView?
): GameEvent
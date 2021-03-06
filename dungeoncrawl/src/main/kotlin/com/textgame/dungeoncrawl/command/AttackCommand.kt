package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Weapon

class AttackCommand(
        val attacker: Creature,
        val defender: Creature,
        val weapon: Weapon?): GameCommand
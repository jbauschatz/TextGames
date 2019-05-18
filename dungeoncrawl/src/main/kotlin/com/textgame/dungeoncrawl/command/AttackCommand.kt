package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.Creature
import com.textgame.dungeoncrawl.model.item.Item

class AttackCommand(
        val attacker: Creature,
        val defender: Creature,
        val weapon: Item?): GameCommand
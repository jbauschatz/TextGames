package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.Creature
import com.textgame.dungeoncrawl.model.item.Item

class AttackEvent(
        val attacker: Creature,
        val defender: Creature,
        val weapon: Item?
): GameEvent
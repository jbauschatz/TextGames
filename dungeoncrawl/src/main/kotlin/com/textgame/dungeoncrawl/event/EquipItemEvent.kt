package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Item

class EquipItemEvent(val actor: Creature, val item: Item): GameEvent
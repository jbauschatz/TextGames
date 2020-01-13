package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Item

class UnequipItemCommand(val actor: Creature, val item: Item): GameCommand
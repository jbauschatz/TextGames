package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Item

class UseItemCommand(val actor: Creature, val item: Item): GameCommand
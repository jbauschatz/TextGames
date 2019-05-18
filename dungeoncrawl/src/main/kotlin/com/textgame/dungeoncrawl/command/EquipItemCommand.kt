package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.Creature
import com.textgame.dungeoncrawl.model.item.Item

class EquipItemCommand(val actor: Creature, val item: Item): GameCommand
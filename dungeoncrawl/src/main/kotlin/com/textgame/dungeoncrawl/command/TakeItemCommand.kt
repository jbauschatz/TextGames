package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.Creature
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location

class TakeItemCommand(val actor: Creature, val item: Item, val location: Location): GameCommand
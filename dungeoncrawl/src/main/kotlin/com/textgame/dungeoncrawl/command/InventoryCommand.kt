package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.creature.Creature

/**
 * Command to list the given actor's inventory
 */
class InventoryCommand(val actor: Creature): GameCommand
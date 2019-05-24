package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.ItemView

/**
 * [GameEvent] which represents a [Creature] equipping an [Item]
 */
class EquipItemEvent(
        val actor: CreatureView,
        val item: ItemView
): GameEvent {

    constructor(actor: Creature, item: Item): this(
            CreatureView(actor),
            ItemView(item)
    )
}
package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Consumable
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.ItemView
import com.textgame.engine.model.verb.Verb

class HealingItemEvent(
        val actor: CreatureView,
        val consumable: ItemView,
        val verb: Verb
): GameEvent {

    constructor(actor: Creature, consumable: Consumable): this(
            CreatureView(actor),
            ItemView(consumable),
            consumable.verb
    )

}
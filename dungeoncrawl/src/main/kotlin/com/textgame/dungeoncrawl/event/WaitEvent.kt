package com.textgame.dungeoncrawl.event

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.view.CreatureView

class WaitEvent(val actor: CreatureView): GameEvent {

    constructor(actor: Creature): this(CreatureView(actor))
}
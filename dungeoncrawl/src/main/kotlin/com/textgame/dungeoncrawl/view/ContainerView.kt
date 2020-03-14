package com.textgame.dungeoncrawl.view

import com.textgame.dungeoncrawl.model.Container
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

class ContainerView private constructor(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns,
        val slots: Map<String, List<ItemView>>
): NamedEntity(id, name, pronouns) {

    constructor(container: Container): this(
            container.id,
            container.name,
            container.pronouns,
            // TODO filter by non-empty slot inventories
            container.slotNames.map { slot ->
                slot to container.getSlot(slot)!!.members.map { ItemView(it) }
            }.toMap()
    )
}
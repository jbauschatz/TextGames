package com.textgame.dungeoncrawl.model.item

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

class Item(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns = Pronouns.THIRD_PERSON_SINGULAR_NEUTER
): NamedEntity(id, name, pronouns)
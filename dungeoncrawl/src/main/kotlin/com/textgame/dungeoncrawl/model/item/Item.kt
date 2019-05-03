package com.textgame.dungeoncrawl.model.item

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

class Item(
        override val name: NounPhrase,
        override val pronouns: Pronouns = Pronouns.THIRD_PERSON_SINGULAR_NEUTER
): NamedEntity
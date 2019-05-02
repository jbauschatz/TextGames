package com.textgame.dungeoncrawl.model

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase

data class Creature(
        override val name: NounPhrase,
        override val pronouns: Pronouns
): NamedEntity
package com.textgame.engine.test

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase

data class TestNamedEntity(
        override val name: NounPhrase,
        override val pronouns: Pronouns
): NamedEntity
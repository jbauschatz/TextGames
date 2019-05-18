package com.textgame.engine.test

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase

class TestNamedEntity(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns
): NamedEntity(id, name, pronouns)
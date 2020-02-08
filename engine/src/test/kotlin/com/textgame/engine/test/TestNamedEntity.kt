package com.textgame.engine.test

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase

class TestNamedEntity(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns = Pronouns.THIRD_PERSON_SINGULAR_NEUTER
): NamedEntity(id, name, pronouns)
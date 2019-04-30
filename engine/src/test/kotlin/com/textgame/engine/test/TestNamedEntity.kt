package com.textgame.engine.test

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase

class TestNamedEntity constructor(
        private val name: NounPhrase,
        private val pronouns: Pronouns
): NamedEntity {

    override fun getName(): NounPhrase = name

    override fun getPronouns(): Pronouns = pronouns
}
package com.textgame.engine.model.nounphrase

import com.textgame.engine.WordUtil

class Pronoun(val value: String): NounPhrase {

    override fun definite(): NounPhrase =
            this

    override fun indefinite(): NounPhrase =
            this

    override fun startsWithVowelSound(): Boolean =
        WordUtil.startsWithVowel(value)
}
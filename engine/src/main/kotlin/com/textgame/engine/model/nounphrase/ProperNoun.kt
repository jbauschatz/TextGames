package com.textgame.engine.model.nounphrase

import com.textgame.engine.WordUtil

class ProperNoun constructor(
        val value: String,
        private val startsWithVowelSound: Boolean
): NounPhrase {

    constructor(value: String): this(value, WordUtil.startsWithVowel(value))

    override fun definite(): NounPhrase =
            this

    override fun indefinite(): NounPhrase =
            this

    override fun head() =
            this

    override fun startsWithVowelSound(): Boolean =
            startsWithVowelSound

}
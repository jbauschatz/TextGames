package com.textgame.engine.model.nounphrase

import com.textgame.engine.WordUtil

data class Noun(
        val value: String,
        private val startsWithVowelSound: Boolean
): NounPhrase {

    constructor(value: String): this(value, WordUtil.startsWithVowel(value))

    override fun definite(): NounPhrase =
            Definite(this)

    override fun indefinite(): NounPhrase =
            Indefinite(this)

    override fun head() =
            this

    override fun startsWithVowelSound(): Boolean =
            startsWithVowelSound
}
package com.textgame.engine.model.nounphrase

class Noun(
        val value: String,
        private val startsWithVowelSound: Boolean
): NounPhrase {

    constructor(value: String): this(value, "aeiouAEIOC".contains(value[0]))

    override fun definite(): NounPhrase =
            Definite(this)

    override fun indefinite(): NounPhrase =
            Indefinite(this)

    override fun startsWithVowelSound(): Boolean =
            startsWithVowelSound
}
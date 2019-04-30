package com.textgame.engine.model.nounphrase

class ProperNoun constructor(
        val value: String,
        private val startsWithVowelSound: Boolean
): NounPhrase {

    constructor(value: String): this(value, "aeiouAEIOC".contains(value[0]))

    override fun definite(): NounPhrase =
            this

    override fun indefinite(): NounPhrase =
            this

    override fun startsWithVowelSound(): Boolean =
            startsWithVowelSound

}
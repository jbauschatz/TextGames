package com.textgame.engine.model.nounphrase

import com.textgame.engine.WordUtil

class Adjective(
        val value: String,
        val stem: NounPhrase,
        private val startsWithVowelSound: Boolean
): NounPhrase {

    constructor(value: String, stem: NounPhrase): this(value, stem, WordUtil.startsWithVowel(value))

    override fun definite(): NounPhrase =
            Definite(this)

    override fun indefinite(): NounPhrase =
            Indefinite(this)

    override fun startsWithVowelSound(): Boolean =
            startsWithVowelSound
}
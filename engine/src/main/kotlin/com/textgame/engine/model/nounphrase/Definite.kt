package com.textgame.engine.model.nounphrase

class Definite(val stem: NounPhrase): NounPhrase {

    override fun definite(): NounPhrase =
            this

    override fun indefinite(): NounPhrase =
            Indefinite(stem)

    override fun head() =
            stem.head()

    override fun startsWithVowelSound(): Boolean =
            stem.startsWithVowelSound()
}
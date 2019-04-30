package com.textgame.engine.model.nounphrase

class Indefinite(val stem: NounPhrase): NounPhrase {

    override fun definite(): NounPhrase =
            Definite(stem)

    override fun indefinite(): NounPhrase =
            this

    override fun startsWithVowelSound(): Boolean =
            stem.startsWithVowelSound()
}
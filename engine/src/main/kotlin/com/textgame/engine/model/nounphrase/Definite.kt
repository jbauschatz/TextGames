package com.textgame.engine.model.nounphrase

class Definite(val stem: NounPhrase, val alwaysDefinite: Boolean = false): NounPhrase {

    override fun definite(): NounPhrase =
            this

    override fun indefinite(): NounPhrase =
            if (alwaysDefinite) this else Indefinite(stem)

    override fun head() =
            stem.head()

    override fun startsWithVowelSound(): Boolean =
            stem.startsWithVowelSound()
}
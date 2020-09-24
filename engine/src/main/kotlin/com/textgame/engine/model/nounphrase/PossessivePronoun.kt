package com.textgame.engine.model.nounphrase

data class PossessivePronoun(val pronoun: Pronoun, val head: NounPhrase) : NounPhrase {
    override fun definite(): NounPhrase = this

    override fun indefinite(): NounPhrase = this

    override fun head(): NounPhrase = head

    override fun startsWithVowelSound(): Boolean = pronoun.startsWithVowelSound()
}
package com.textgame.engine.model.nounphrase

interface NounPhrase {

    fun definite(): NounPhrase

    fun indefinite(): NounPhrase

    /**
     * Gets the head of the noun phrase (ie, the simple noun at the root of a complex noun phrase)
     */
    fun head(): NounPhrase

    fun startsWithVowelSound(): Boolean
}
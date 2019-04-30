package com.textgame.engine.model.nounphrase

interface NounPhrase {

    fun definite(): NounPhrase

    fun indefinite(): NounPhrase

    fun startsWithVowelSound(): Boolean
}
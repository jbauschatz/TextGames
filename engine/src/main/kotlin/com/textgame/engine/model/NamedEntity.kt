package com.textgame.engine.model

import com.textgame.engine.model.nounphrase.NounPhrase

interface NamedEntity {

    fun getName(): NounPhrase

    fun getPronouns(): Pronouns
}
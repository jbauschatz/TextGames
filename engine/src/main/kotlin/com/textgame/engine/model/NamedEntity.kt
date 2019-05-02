package com.textgame.engine.model

import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

interface NamedEntity {

    fun getName(): NounPhrase

    fun getPronouns(): Pronouns
}
package com.textgame.engine.model

import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

interface NamedEntity {

    val name: NounPhrase

    val pronouns: Pronouns
}
package com.textgame.engine.model

import com.textgame.engine.model.nounphrase.NounPhrase

/**
 * Represents a specific instance of a [NamedEntity] being referred to by a name
 */
data class Name(
        val name: NounPhrase,
        val referent: NamedEntity
)
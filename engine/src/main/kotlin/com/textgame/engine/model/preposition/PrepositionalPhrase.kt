package com.textgame.engine.model.preposition

import com.textgame.engine.model.NamedEntity

data class PrepositionalPhrase(
        val preposition: String,
        val objectOfPreposition: NamedEntity
)
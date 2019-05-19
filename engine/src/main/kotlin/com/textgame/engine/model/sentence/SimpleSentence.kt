package com.textgame.engine.model.sentence

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.preposition.PrepositionalPhrase

data class SimpleSentence(

        val subject: NamedEntity,

        val verb: String,

        val directObject: NamedEntity?,

        val prepositionalPhrase: PrepositionalPhrase?
) {
    constructor(subject: NamedEntity, verb: String): this(subject, verb, null, null)

    constructor(subject: NamedEntity, verb: String, directObject: NamedEntity): this(subject, verb, directObject, null)
}
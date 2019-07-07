package com.textgame.engine.model.sentence

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.verb.Verb

data class SimpleSentence(

        val subject: NamedEntity,

        val verb: Verb,

        val directObject: NamedEntity?,

        val prepositionalPhrase: PrepositionalPhrase?
): Sentence {
    constructor(subject: NamedEntity, verb: Verb): this(subject, verb, null, null)

    constructor(subject: NamedEntity, verb: Verb, directObject: NamedEntity): this(subject, verb, directObject, null)
}
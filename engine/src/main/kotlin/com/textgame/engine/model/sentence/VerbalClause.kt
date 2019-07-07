package com.textgame.engine.model.sentence

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.verb.Verb

/**
 * Partial sentence including a Verb, and optional Direct Object and Prepositional Phrase.
 *
 * This is like a complete sentence except missing a Subject.
 */
data class VerbalClause(
        val verb: Verb,
        val directObject: NamedEntity? = null,
        val prepositionalPhrase: PrepositionalPhrase? = null
) {

    constructor(sentence: SimpleSentence): this(
            sentence.verb,
            sentence.directObject,
            sentence.prepositionalPhrase
    )
}
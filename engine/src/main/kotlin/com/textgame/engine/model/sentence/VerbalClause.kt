package com.textgame.engine.model.sentence

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.preposition.PrepositionalPhrase

/**
 * Partial sentence including a Verb, and optional Direct Object and Prepositional Phrase.
 *
 * This is like a complete sentence except missing a Subject.
 */
class VerbalClause(
        val verb: String,
        val directObject: NamedEntity? = null,
        val prepositionalPhrase: PrepositionalPhrase? = null
) {
}
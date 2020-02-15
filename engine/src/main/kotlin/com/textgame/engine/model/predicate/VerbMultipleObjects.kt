package com.textgame.engine.model.predicate

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.verb.Verb

/**
 * Predicate including a Verb, multiple Direct Objects, and an optional Prepositional Phrase.
 *
 * This is like a complete sentence except missing a Subject.
 */
data class VerbMultipleObjects(
        val verb: Verb,
        val directObjects: List<NamedEntity>,
        val prepositionalPhrase: PrepositionalPhrase? = null
) : SentencePredicate
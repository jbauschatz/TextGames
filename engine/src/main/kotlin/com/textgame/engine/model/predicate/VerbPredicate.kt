package com.textgame.engine.model.predicate

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.verb.Verb

/**
 * Predicate including a Verb, and optional Direct Object and Prepositional Phrase.
 *
 * This is like a complete sentence except missing a Subject.
 */
data class VerbPredicate(
        val verb: Verb,
        val directObject: NamedEntity? = null,
        val prepositionalPhrase: PrepositionalPhrase? = null
) : SentencePredicate
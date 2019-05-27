package com.textgame.engine.model.sentence

import com.textgame.engine.model.NamedEntity

/**
 * A sentence with a single Subject and multiple Verbal Clauses.
 *
 * ie, "The dog [runs home] and [eats his food]." The multiple sub-clauses will probably be joined by an "and" during
 * sentence realization.
 */
class MultipleVerbalClauses(
        val subject: NamedEntity,
        val clauses: List<VerbalClause>
): Sentence
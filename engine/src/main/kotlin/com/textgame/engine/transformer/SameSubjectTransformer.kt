package com.textgame.engine.transformer

import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.predicate.Predicates
import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence

/**
 * Transformer which combines sentences sharing a subject, but different verbs
 *
 * The resulting sentence has one [VerbPredicate] from each input sentence.
 *
 * Example:
 * "Jack goes up the hill" + "Jack eats an apple"
 * -> "Jack goes up the hill and eats the apple"
 */
object SameSubjectTransformer: SentenceTransformer {

    override fun canTransform(vararg sentences: SimpleSentence): Boolean {
        if (sentences.size < 2)
            return false

        val firstSentence = sentences[0]
        if (firstSentence.predicate !is VerbPredicate)
            return false
        val subject = firstSentence.subject
        val verb = firstSentence.predicate.verb

        for (i in 1 until sentences.size) {
            val sentence = sentences[i]

            // Must have the same subject as the first
            if (sentence.subject != subject)
                return false

            // Must have a different verb than the first
            if (sentence.predicate is VerbPredicate && sentence.predicate.verb == verb)
                return false
        }

        return true
    }

    override fun transform(vararg sentences: SimpleSentence): Sentence {
        val firstSentence = sentences[0]

        return SimpleSentence(
                firstSentence.subject,
                Predicates(
                        sentences.map { it.predicate as VerbPredicate }
                )
        )
    }
}
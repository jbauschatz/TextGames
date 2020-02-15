package com.textgame.engine.transformer

import com.textgame.engine.model.predicate.VerbMultipleObjects
import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence

/**
 * Transformer which combines sentences sharing a subject and verb, but different objects
 *
 * The resulting sentence has one direct object or preposition from each input sentence.
 *
 * Example:
 * "Jack sees Spot" + "Jack sees an apple"
 * -> "Jack sees Jane and an apple"
 */
object SameSubjectVerbTransformer: SentenceTransformer {

    override fun canTransform(vararg sentences: SimpleSentence): Boolean {
        if (sentences.size < 2)
            return false

        val firstSentence = sentences[0]
        if (firstSentence.predicate !is VerbPredicate)
            return false

        // Sentence must have a direct object and no preposition
        if (firstSentence.predicate.directObject == null || firstSentence.predicate.prepositionalPhrase != null)
            return false

        val subject = firstSentence.subject
        val verb = firstSentence.predicate.verb
        val directObject = firstSentence.predicate.directObject

        for (i in 1 until sentences.size) {
            val sentence = sentences[i]

            // Must have the same subject
            if (!sentence.subject.equals(subject)) {
                return false
            }

            // Predicate must have the same verb
            if (sentence.predicate !is VerbPredicate) {
                return false
            }
            if (sentence.predicate.verb != verb)
                return false

            // Predicate must have a direct object
            if (sentence.predicate.directObject == null || sentence.predicate.prepositionalPhrase != null)
                return false

            // Direct object must be different
            if (sentence.predicate.directObject == directObject)
                return false
        }

        return true
    }

    override fun transform(vararg sentences: SimpleSentence): Sentence {
        val firstSentence = sentences[0]

        return SimpleSentence(
                firstSentence.subject,
                VerbMultipleObjects(
                        (firstSentence.predicate as VerbPredicate).verb,
                        sentences.map { (it.predicate as VerbPredicate).directObject!! }
                )
        )
    }
}
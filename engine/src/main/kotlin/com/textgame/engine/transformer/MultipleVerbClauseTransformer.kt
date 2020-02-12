package com.textgame.engine.transformer

import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.predicate.VerbPredicates
import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.verb.Verb

object MultipleVerbClauseTransformer: SentenceTransformer {

    override fun canTransform(vararg sentences: SimpleSentence): Boolean {
        if (sentences.size < 2)
            return false

        val firstSentence = sentences[0]
        if (firstSentence.predicate !is VerbPredicate)
            return false

        for (i in 1 until sentences.size) {
            val sentence = sentences[i]

            // This sentence must have the same sentence as the first
            if (!sentence.subject.equals(firstSentence.subject)) {
                return false
            }
        }

        return true
    }

    override fun transform(vararg sentences: SimpleSentence): Sentence {
        val firstSentence = sentences[0]

        return SimpleSentence(
                firstSentence.subject,
                VerbPredicates(
                        sentences.map { it.predicate as VerbPredicate }
                )
        )
    }
}
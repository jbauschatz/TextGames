package com.textgame.engine.transformer

import com.textgame.engine.model.sentence.MultipleVerbalClauses
import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.sentence.VerbalClause

object MultipleVerbClauseTransformer: SentenceTransformer {

    override fun canTransform(vararg sentences: SimpleSentence): Boolean {
        if (sentences.size < 2)
            return false

        val firstSentence = sentences[0]

        for (i in 1 until sentences.size) {
            val sentence = sentences[i]
            if (!sentence.subject.equals(firstSentence.subject)) {
                return false
            }
            if (sentence.verb.equals(firstSentence.verb)) {
                return false
            }
        }

        return true
    }

    override fun transform(vararg sentences: SimpleSentence): Sentence {
        val firstSentence = sentences[0]

        return MultipleVerbalClauses(
                firstSentence.subject,
                sentences.map { VerbalClause(it) }
        )
    }
}
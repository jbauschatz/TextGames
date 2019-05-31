package com.textgame.engine.narrator

import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.transformer.MultipleVerbClauseTransformer
import com.textgame.engine.transformer.SentenceTransformer
import java.util.*

/**
 * Class which receives [SimpleSentence] instances in chronological order, and embellishes them
 */
class Narrator(
        private val sentenceRealizer: SentenceRealizer
) {

    private val paragraphs: MutableList<Paragraph> = mutableListOf()

    private val recentSentences: MutableList<SimpleSentence> = mutableListOf()

    private val transformers: List<SentenceTransformer> = listOf(
            MultipleVerbClauseTransformer
    )

    /**
     * Adds a [SimpleSentence] to the narrative.
     *
     * It may be embellished/transformed later on, depending on the configured rules and what other [SimpleSentence]s
     * are present.
     */
    fun narrate(sentence: SimpleSentence) {
        recentSentences.add(sentence)
    }

    /**
     * Empties the cache of paragraphs, regardless of whether or not the narrator is finished embellishing, or would
     * naturally produce a paragraph break at this point.
     *
     * This allows the ability to control the pace of narration externally.
     */
    fun flushParagraphs(): List<Paragraph> {
        // Build a paragraph containing any straggling sentences
        if (recentSentences.isNotEmpty()) {
            val transformedSentences = transform(recentSentences)

            val lastParagraph = Paragraph(
                    transformedSentences.map { sentenceRealizer.realize(it) }
            )
            recentSentences.clear()
            paragraphs.add(lastParagraph)
        }

        val returnParagraphs = LinkedList(paragraphs)
        paragraphs.clear()

        return returnParagraphs
    }

    private fun transform(sentences: MutableList<SimpleSentence>): List<Sentence> {
        val transformedSentences = mutableListOf<Sentence>()

        while(sentences.size > 1) {
            val firstSentence = sentences.removeAt(0)
            val nextSentence = sentences[0]

            var didTransform = false

            transformers.forEach {
                if (!didTransform && it.canTransform(firstSentence, nextSentence)) {
                    val transformed = it.transform(firstSentence, nextSentence)
                    transformedSentences.add(transformed)
                    sentences.removeAt(0)
                    didTransform = true
                }
            }

            if (!didTransform) {
                // Add the first sentences, unaltered, to the processed list
                transformedSentences.add(firstSentence)
            }
        }

        if (sentences.isNotEmpty())
            transformedSentences.addAll(sentences)

        return transformedSentences
    }
}
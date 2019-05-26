package com.textgame.engine.narrator

import com.textgame.engine.model.sentence.SimpleSentence
import java.util.*

/**
 * Class which receives [SimpleSentence] instances in chronological order, and embellishes them
 */
class Narrator(
        private val sentenceRealizer: SentenceRealizer
) {

    private val paragraphs: MutableList<Paragraph> = mutableListOf()

    private val recentSentences: MutableList<SimpleSentence> = mutableListOf()

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
            val lastParagraph = Paragraph(
                    recentSentences.map { sentenceRealizer.realize(it) }
            )
            recentSentences.clear()
            paragraphs.add(lastParagraph)
        }

        val returnParagraphs = LinkedList(paragraphs)
        paragraphs.clear()

        return returnParagraphs
    }
}
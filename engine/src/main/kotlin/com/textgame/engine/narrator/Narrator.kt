package com.textgame.engine.narrator

import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.transformer.SameSubjectTransformer
import com.textgame.engine.transformer.SameSubjectVerbTransformer
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
            SameSubjectTransformer,
            SameSubjectVerbTransformer
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

    /**
     * Transform the [SimpleSentence]s into more complex using the configured [SentenceTransformer]s.
     *
     * This works through the sentences left-to-right, finding the longest possible chain that can be transformed.
     *
     * It is "greedy" in the sense that it will always pick the left-most chain of sentences that can be transformed,
     * so it's not always guaranteed to transform the most sentences possible.
     */
    private fun transform(sentences: MutableList<SimpleSentence>): List<Sentence> {
        val transformedSentences = mutableListOf<Sentence>()

        while (sentences.size > 1) {
            val firstSentence = sentences.removeAt(0)
            var longestTransformation: Transformation? = null

            // Build up a chain of sentences starting with this one
            val chainToTransform = mutableListOf(firstSentence)
            for (sentence in sentences) {
                chainToTransform += sentence

                // See if any Transformer can transform this chain
                var hasTransformation = false
                transformers.forEach {
                    if (it.canTransform(*chainToTransform.toTypedArray())) {
                        longestTransformation = Transformation(chainToTransform.toList(), it)
                        hasTransformation = true
                    }
                }
                if (!hasTransformation)
                    break
            }

            if (longestTransformation != null) {
                // If a transformation is available, remove those sentences and add the transformed version
                longestTransformation!!.chain.forEach { sentences.remove(it) }
                val transformedSentence = longestTransformation!!.transformer.transform(*(longestTransformation!!.chain.toTypedArray()))
                transformedSentences += transformedSentence
            } else {
                // Add the sentence, unaltered, to the processed list
                transformedSentences += firstSentence
            }
        }

        // Add any remaining, untransformed sentences
        if (sentences.isNotEmpty())
            transformedSentences.addAll(sentences)

        return transformedSentences
    }

    private data class Transformation(
            val chain: List<SimpleSentence>,
            val transformer: SentenceTransformer
    )
}
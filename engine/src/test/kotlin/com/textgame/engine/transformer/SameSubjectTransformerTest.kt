package com.textgame.engine.transformer

import com.textgame.engine.model.predicate.Predicates
import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.test.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class SameSubjectTransformerTest {

    @Test
    fun canTransform_valid_twoSentences() {
        // GIVEN two sentences with the same subject, but different verbs
        val sentences = arrayOf(JACK_GOES_UPTHEHILL, JACK_EATS_APPLE)

        // WHEN checking if the transformer can transform
        val applies = SameSubjectTransformer.canTransform(*sentences)

        // EXPECT the Transformer to apply
        assertThat(applies, equalTo(true))
    }

    @Test
    fun canTransform_valid_threeSentences() {
        // GIVEN three sentences with the same subject, but different verbs
        val sentences = arrayOf(JACK_GOES_UPTHEHILL, JACK_EATS_APPLE, JACK_DRINKS_WATER)

        // WHEN checking if the transformer can transform
        val applies = SameSubjectTransformer.canTransform(*sentences)

        // EXPECT the Transformer to apply
        assertThat(applies, equalTo(true))
    }

    @Test
    fun canTransform_invalid_sameVerb() {
        // GIVEN two sentences with the same subject, and same verbs
        val sentences = arrayOf(JACK_EATS_APPLE, JACK_EATS_COOKIE)

        // WHEN checking if the transformer can transform
        val applies = SameSubjectTransformer.canTransform(*sentences)

        // EXPECT the Transformer not to apply
        assertThat(applies, equalTo(false))
    }

    @Test
    fun canTransform_invalid_differentSubjects() {
        // GIVEN two sentences with different subjects
        val sentences = arrayOf(JACK_GOES_UPTHEHILL, JILL_EATS_APPLE)

        // WHEN checking if the transformer can transform
        val applies = SameSubjectTransformer.canTransform(*sentences)

        // EXPECT the Transformer not to apply
        assertThat(applies, equalTo(false))
    }

    @Test
    fun transform_valid_twoSentences() {
        // GIVEN two sentences with the same subject, but different verbs
        val sentences = arrayOf(JACK_GOES_UPTHEHILL, JACK_EATS_APPLE)

        // WHEN applying the transformation
        val transformed = SameSubjectTransformer.transform(*sentences)

        // EXPECT the a combined sentence to result
        val expected: Sentence = SimpleSentence(
                JACK,
                Predicates(listOf(
                        VerbPredicate(GO, prepositionalPhrase = PrepositionalPhrase("up", HILL)),
                        VerbPredicate(EAT, APPLE)
                ))
        )
        assertThat(transformed, equalTo(expected))
    }

    @Test
    fun transform_threeSentencesWithSameSubject() {
        // GIVEN three sentences with the same subject, but different verbs
        val sentences = arrayOf(JACK_GOES_UPTHEHILL, JACK_EATS_APPLE, JACK_DRINKS_WATER)

        // WHEN applying the transformation
        val transformed = SameSubjectTransformer.transform(*sentences)

        // EXPECT the combined sentence to result
        val expected: Sentence = SimpleSentence(
                JACK,
                Predicates(listOf(
                        VerbPredicate(GO, prepositionalPhrase = PrepositionalPhrase("up", HILL)),
                        VerbPredicate(EAT, APPLE),
                        VerbPredicate(DRINK, WATER)
                ))
        )
        assertThat(transformed, equalTo(expected))
    }
}
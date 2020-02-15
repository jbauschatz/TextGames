package com.textgame.engine.transformer

import com.textgame.engine.model.predicate.VerbMultipleObjects
import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.test.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class SameSubjectVerbTransformerTest {

    @Test
    fun canTransform_valid_twoSentences() {
        // GIVEN two sentences with the same subject and verb, and with different objects
        val sentences = arrayOf(JACK_EATS_APPLE, JACK_EATS_COOKIE)

        // WHEN checking if the Transformer can transform
        val applies = SameSubjectVerbTransformer.canTransform(*sentences)

        // EXPECT the Transformer to apply
        assertThat(applies, equalTo(true))
    }

    @Test
    fun canTransform_valid_threeSentences() {
        // GIVEN three sentences with the same subject and verb, and different objects
        val sentences = arrayOf(JACK_EATS_APPLE, JACK_EATS_COOKIE, JACK_EATS_ORANGE)

        // WHEN checking if the Transformer can transform
        val applies = SameSubjectVerbTransformer.canTransform(*sentences)

        // EXPECT the Transformer to apply
        assertThat(applies, equalTo(true))
    }

    @Test
    fun canTransform_invalid_differentVerb() {
        // GIVEN two sentences with the same subject, but different verbs
        val sentences = arrayOf(JACK_GOES_UPTHEHILL, JACK_EATS_APPLE)

        // WHEN checking if the Transformer can transform
        val applies = SameSubjectVerbTransformer.canTransform(*sentences)

        // EXPECT the Transformer to apply
        assertThat(applies, equalTo(false))
    }

    @Test
    fun canTransform_invalid_missingObject() {
        // GIVEN two sentences, where one is missing a direct object
        val sentences = arrayOf(JACK_GOES_UPTHEHILL, JACK_EATS_COOKIE)

        // WHEN checking if the Transformer can transform
        val applies = SameSubjectVerbTransformer.canTransform(*sentences)

        // EXPECT the Transformer not to apply
        assertThat(applies, equalTo(false))
    }

    @Test
    fun canTransform_invalid_sameObject() {
        // GIVEN two sentences with the same subject, verb, and object
        val sentences = arrayOf(JACK_EATS_APPLE, JACK_EATS_APPLE)

        // WHEN checking if the Transformer can transform
        val applies = SameSubjectVerbTransformer.canTransform(*sentences)

        // EXPECT the Transformer not to apply
        assertThat(applies, equalTo(false))
    }

    @Test
    fun canTransform_invalid_differentSubject() {
        // GIVEN two sentences with different subjects
        val sentences = arrayOf(JACK_EATS_COOKIE, JILL_EATS_APPLE)

        // WHEN checking if the Transformer can transform
        val applies = SameSubjectVerbTransformer.canTransform(*sentences)

        // EXPECT the Transformer not to apply
        assertThat(applies, equalTo(false))
    }

    @Test
    fun transform_twoSentences() {
        // GIVEN two sentences with the same subject and verb, but different objects
        val sentences = arrayOf(JACK_EATS_APPLE, JACK_EATS_ORANGE)

        // WHEN applying the transformation
        val transformed = SameSubjectVerbTransformer.transform(*sentences)

        // EXPECT the a combined sentence to result
        val expected: Sentence = SimpleSentence(
                JACK,
                VerbMultipleObjects(
                        EAT,
                        listOf(APPLE, ORANGE)
                )
        )
        assertThat(transformed, equalTo(expected))
    }

    @Test
    fun transform_threeSentences() {
        // GIVEN three sentences with the same subject and verb, but different direct objects
        val sentences = arrayOf(JACK_EATS_COOKIE, JACK_EATS_ORANGE, JACK_EATS_APPLE)

        // WHEN applying the transformation
        val transformed = SameSubjectVerbTransformer.transform(*sentences)

        // EXPECT the combined sentence to result
        val expected: Sentence = SimpleSentence(
                JACK,
                VerbMultipleObjects(
                        EAT,
                        listOf(COOKIE, ORANGE, APPLE)
                )
        )
        assertThat(transformed, equalTo(expected))
    }
}
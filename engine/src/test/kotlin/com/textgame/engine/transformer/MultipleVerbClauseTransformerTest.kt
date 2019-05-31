package com.textgame.engine.transformer

import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.MultipleVerbalClauses
import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.sentence.VerbalClause
import com.textgame.engine.test.TestNamedEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class MultipleVerbClauseTransformerTest {

    @Test
    fun canTransform_twoSentencesWithSameSubject() {
        // GIVEN two sentences with the same subject, but different verbs
        val sentenceA = SimpleSentence(
                TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE),
                "goes",
                directObject = null,
                prepositionalPhrase = PrepositionalPhrase("up", TestNamedEntity(2, Noun("hill"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER))
        )
        val sentenceB = SimpleSentence(
                TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE),
                "eats",
                TestNamedEntity(2, Noun("apple"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
        )

        // WHEN checking if the transformer can transform
        val applies = MultipleVerbClauseTransformer.canTransform(sentenceA, sentenceB)

        // EXPECT the Transformer to apply
        assertThat(applies, `is`(equalTo(true)))
    }

    @Test
    fun canTransform_twoSentencesWithDifferentSubject() {
        // GIVEN two sentences with different subjects
        val sentenceA = SimpleSentence(
                TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE),
                "goes",
                directObject = null,
                prepositionalPhrase = PrepositionalPhrase("up", TestNamedEntity(2, Noun("hill"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER))
        )
        val sentenceB = SimpleSentence(
                TestNamedEntity(3, ProperNoun("Jill"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE),
                "eats",
                TestNamedEntity(2, Noun("apple"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
        )

        // WHEN checking if the transformer can transform
        val applies = MultipleVerbClauseTransformer.canTransform(sentenceA, sentenceB)

        // EXPECT the Transformer to apply
        assertThat(applies, `is`(equalTo(false)))
    }

    @Test
    fun transform_twoSentencesWithSameSubject() {
        // GIVEN two sentences with the same subject, but different verbs
        val jack = TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val hill = TestNamedEntity(2, Noun("hill"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
        val apple = TestNamedEntity(3, Noun("apple"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)

        val sentenceA = SimpleSentence(
                jack,
                "goes",
                directObject = null,
                prepositionalPhrase = PrepositionalPhrase("up", hill)
        )
        val sentenceB = SimpleSentence(
                jack,
                "eats",
                apple
        )

        // WHEN checking if the transformer can transform
        val transformed = MultipleVerbClauseTransformer.transform(sentenceA, sentenceB)

        // EXPECT the Transformer to apply
        val expected: Sentence = MultipleVerbalClauses(
                jack,
                listOf(
                        VerbalClause("goes", prepositionalPhrase = PrepositionalPhrase("up", hill)),
                        VerbalClause("eats", apple)
                )
        )
        assertThat(transformed, `is`(equalTo(expected)))
    }
}
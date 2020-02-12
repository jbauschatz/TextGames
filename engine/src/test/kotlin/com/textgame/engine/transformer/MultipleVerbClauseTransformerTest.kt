package com.textgame.engine.transformer

import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.predicate.VerbPredicates
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.verb.Verb
import com.textgame.engine.test.TestNamedEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class MultipleVerbClauseTransformerTest {

    @Test
    fun canTransform_twoSentencesWithSameSubject() {
        // GIVEN two sentences with the same subject, but different verbs
        val jack = TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)

        val sentenceA = SimpleSentence(
                jack,
                VerbPredicate(
                        Verb("goes", "go"),
                        prepositionalPhrase = PrepositionalPhrase("up", TestNamedEntity(2, Noun("hill"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER))
                )
        )
        val sentenceB = SimpleSentence(
                jack,
                VerbPredicate(
                        Verb("eats", "eat"),
                        TestNamedEntity(2, Noun("apple"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
                )
        )

        // WHEN checking if the transformer can transform
        val applies = MultipleVerbClauseTransformer.canTransform(sentenceA, sentenceB)

        // EXPECT the Transformer to apply
        assertThat(applies, equalTo(true))
    }

    @Test
    fun canTransform_twoSentencesWithDifferentSubject() {
        // GIVEN two sentences with different subjects
        val sentenceA = SimpleSentence(
                TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE),
                VerbPredicate(
                        Verb("goes", "go"),
                        prepositionalPhrase = PrepositionalPhrase("up", TestNamedEntity(2, Noun("hill"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER))
                )
        )
        val sentenceB = SimpleSentence(
                TestNamedEntity(3, ProperNoun("Jill"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE),
                VerbPredicate(
                        Verb("eats", "eat"),
                        TestNamedEntity(2, Noun("apple"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
                )
        )

        // WHEN checking if the transformer can transform
        val applies = MultipleVerbClauseTransformer.canTransform(sentenceA, sentenceB)

        // EXPECT the Transformer to apply
        assertThat(applies, equalTo(false))
    }

    @Test
    fun transform_twoSentencesWithSameSubject() {
        // GIVEN two sentences with the same subject, but different verbs
        val jack = TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val hill = TestNamedEntity(2, Noun("hill"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
        val apple = TestNamedEntity(3, Noun("apple"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)

        val sentenceA = SimpleSentence(
                jack,
                VerbPredicate(
                        Verb("goes", "go"),
                        prepositionalPhrase = PrepositionalPhrase("up", hill)
                )
        )
        val sentenceB = SimpleSentence(
                jack,
                VerbPredicate(
                        Verb("eats", "eat"),
                        apple
                )
        )

        // WHEN applying the transformation
        val transformed = MultipleVerbClauseTransformer.transform(sentenceA, sentenceB)

        // EXPECT the a combined sentence to result
        val expected: Sentence = SimpleSentence(
                jack,
                VerbPredicates(listOf(
                        VerbPredicate(Verb("goes", "go"), prepositionalPhrase = PrepositionalPhrase("up", hill)),
                        VerbPredicate(Verb("eats", "eat"), apple)
                ))
        )
        assertThat(transformed, equalTo(expected))
    }

    @Test
    fun transform_threeSentencesWithSameSubject() {
        // GIVEN three sentences with the same subject, but different verbs
        val jack = TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val hill = TestNamedEntity(2, Noun("hill"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
        val apple = TestNamedEntity(3, Noun("apple"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
        val bug = TestNamedEntity(4, Noun("bug"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)

        val sentenceA = SimpleSentence(
                jack,
                VerbPredicate(
                        Verb("goes", "go"),
                        prepositionalPhrase = PrepositionalPhrase("up", hill)
                )
        )
        val sentenceB = SimpleSentence(
                jack,
                VerbPredicate(
                        Verb("eats", "eat"),
                        apple
                )
        )
        val sentenceC = SimpleSentence(
                jack,
                VerbPredicate(
                        Verb("catches", "catch"),
                        bug
                )
        )

        // WHEN applying the transformation
        val transformed = MultipleVerbClauseTransformer.transform(sentenceA, sentenceB, sentenceC)

        // EXPECT the a combined sentence to result
        val expected: Sentence = SimpleSentence(
                jack,
                VerbPredicates(listOf(
                        VerbPredicate(Verb("goes", "go"), prepositionalPhrase = PrepositionalPhrase("up", hill)),
                        VerbPredicate(Verb("eats", "eat"), apple),
                        VerbPredicate(Verb("catches", "catch"), bug)
                ))
        )
        assertThat(transformed, equalTo(expected))
    }
}
package com.textgame.engine

import com.textgame.engine.model.Pronouns
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.test.TestNamedEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NarratorTest {

    private lateinit var narrator: Narrator

    @BeforeEach
    fun beforeEach() {
        narrator = Narrator()
    }

    @Test
    fun writeSentence_properSubjectAndObject() {
        // GIVEN a Sentence whose Subject and Object are Proper Nouns
        val sentence = SimpleSentence(
                TestNamedEntity(ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE),
                "saw",
                TestNamedEntity(ProperNoun("Jill"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        )

        // WHEN writing the sentence
        val string = narrator.writeSentence(sentence)

        // EXPECT the Subject and Direct Object to be referenced by their proper names
        assertThat(string, equalTo("Jack saw Jill."))
    }

    @Test
    fun writeSentence_nounSubjectAndObject() {
        // GIVEN a Sentence whose Subject and Object are basic Nouns
        val sentence = SimpleSentence(
                TestNamedEntity(Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE),
                "chased",
                TestNamedEntity(Noun("ball"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        )

        // WHEN writing the sentence
        val string = narrator.writeSentence(sentence)

        // EXPECT the Subject and Direct Object to be indefinite
        assertThat(string, equalTo("A dog chased a ball."))
    }

}
package com.textgame.engine

import com.textgame.engine.model.Pronouns
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.narrator.NarrativeContext
import com.textgame.engine.narrator.Narrator
import com.textgame.engine.test.TestNamedEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NarratorTest {

    private lateinit var narrativeContext: NarrativeContext
    private lateinit var narrator: Narrator

    /**
     * Initializes each test case with a new [Narrator] and an empty [NarrativeContext]
     */
    @BeforeEach
    fun beforeEach() {
        narrativeContext = NarrativeContext()
        narrator = Narrator(narrativeContext)
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
    fun writeSentence_unknownNouns() {
        // GIVEN a Sentence whose Subject and Object are Nouns, and unknown in the Narrative context
        val subject = TestNamedEntity(Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val directObject = TestNamedEntity(Noun("ball"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)

        val sentence = SimpleSentence(
                subject,
                "chased",
                directObject
        )

        // WHEN writing the sentence
        val string = narrator.writeSentence(sentence)

        // EXPECT the Subject and Direct Object to be indefinite, and now added to the Narrative Context
        assertThat(string, equalTo("A dog chased a ball."))
        assertThat("Subject should be in narrative context", narrativeContext.isKnownEntity(subject), equalTo(true))
        assertThat("DirectObject should be in narrative context", narrativeContext.isKnownEntity(directObject), equalTo(true))
    }

    @Test
    fun writeSentence_knownNouns() {
        // GIVEN a Sentence whose Subject and Object are Nouns known in the NarrativeContext
        val subject = TestNamedEntity(Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val directObject = TestNamedEntity(Noun("ball"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)

        narrativeContext.addKnownEntity(subject)
        narrativeContext.addKnownEntity(directObject)

        val sentence = SimpleSentence(
                subject,
                "chased",
                directObject
        )

        // WHEN writing the sentence
        val string = narrator.writeSentence(sentence)

        // EXPECT the Subject and Direct Object to be definite
        assertThat(string, equalTo("The dog chased the ball."))

        assertThat("Subject should be in narrative context", narrativeContext.isKnownEntity(subject), equalTo(true))
        assertThat("DirectObject should be in narrative context", narrativeContext.isKnownEntity(directObject), equalTo(true))
    }

}
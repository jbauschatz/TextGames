package com.textgame.engine.narrator

import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.SimpleSentence
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
                TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE),
                "saw",
                TestNamedEntity(2, ProperNoun("Jill"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        )

        // WHEN writing the sentence
        val string = narrator.writeSentence(sentence)

        // EXPECT the Subject and Direct Object to be referenced by their proper names
        assertThat(string, equalTo("Jack saw Jill."))
    }

    @Test
    fun writeSentence_unknownNouns() {
        // GIVEN a Sentence whose Subject and Object are Nouns, and unknown in the Narrative context
        val subject = TestNamedEntity(1, Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val directObject = TestNamedEntity(2, Noun("ball"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)

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
        val subject = TestNamedEntity(1, Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val directObject = TestNamedEntity(2, Noun("ball"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)

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

    @Test
    fun writeSentence_reflexive() {
        // GIVEN a Sentence whose Subject and Object are the same entity
        val subjectObject = TestNamedEntity(1, Noun("girl"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)

        val sentence = SimpleSentence(
                subjectObject,
                "hurt",
                subjectObject
        )

        // WHEN writing the sentence
        val string = narrator.writeSentence(sentence)

        // EXPECT the Direct Object to be the reflexive pronoun
        assertThat(string, equalTo("A girl hurt herself."))

        assertThat("Subject should be in narrative context", narrativeContext.isKnownEntity(subjectObject), equalTo(true))
    }

    @Test
    fun writeSentence_pronounOverride() {
        // GIVEN a Sentence whose Subject has an overridden pronoun
        val subject = TestNamedEntity(1, Noun("boy"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        narrator.overridePronouns(subject, Pronouns.SECOND_PERSON_SINGULAR)

        val sentence = SimpleSentence(
                subject,
                "draw",
                TestNamedEntity(2, Noun("sword"), Pronouns.THIRD_PERSON_PLURAL_NEUTER)
        )

        // WHEN writing the sentence
        val string = narrator.writeSentence(sentence)

        // EXPECT the Subject to use the overridden pronouns
        assertThat(string, equalTo("You draw a sword."))
    }

    @Test
    fun writeSentence_reflexiveWithOverride() {
        // GIVEN a Sentence whose Subject and Object are the same entity
        val subjectObject = TestNamedEntity(1, Noun("girl"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        narrator.overridePronouns(subjectObject, Pronouns.SECOND_PERSON_SINGULAR)

        val sentence = SimpleSentence(
                subjectObject,
                "hurt",
                subjectObject
        )

        // WHEN writing the sentence
        val string = narrator.writeSentence(sentence)

        // EXPECT the Subject and Direct Object to use the specified pronouns, and the DO to be reflexive
        assertThat(string, equalTo("You hurt yourself."))
    }

    @Test
    fun writeSentence_prepositionalPhrase() {
        // GIVEN a Sentence with a Subject, Object, and Prepositional Phrase
        val subject = TestNamedEntity(1, Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        val directObject = TestNamedEntity(2, Noun("ball"), Pronouns.THIRD_PERSON_PLURAL_NEUTER)
        val objectOfPreposition = TestNamedEntity(3, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)

        val sentence = SimpleSentence(
                subject,
                "gives",
                directObject,
                PrepositionalPhrase("to", objectOfPreposition)
        )

        // WHEN writing the sentence
        val string = narrator.writeSentence(sentence)

        // EXPECT the prepositional phrase to be included at the end of the sentence
        assertThat(string, equalTo("A dog gives a ball to Jack."))
        assertThat(
                "Object of Preposition should be in narrative context",
                narrativeContext.isKnownEntity(objectOfPreposition),
                equalTo(true)
        )
    }

    @Test
    fun writeSentence_prepositionalPhrase_override() {
        // GIVEN a Sentence with a Subject, Object, and Prepositional Phrase whose subject has overridden pronouns
        val subject = TestNamedEntity(1, Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        val directObject = TestNamedEntity(2, Noun("ball"), Pronouns.THIRD_PERSON_PLURAL_NEUTER)
        val objectOfPreposition = TestNamedEntity(3, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)

        narrator.overridePronouns(objectOfPreposition, Pronouns.THIRD_PERSON_SINGULAR_FEMININE)

        val sentence = SimpleSentence(
                subject,
                "gives",
                directObject,
                PrepositionalPhrase("to", objectOfPreposition)
        )

        // WHEN writing the sentence
        val string = narrator.writeSentence(sentence)

        // EXPECT the object of the preposition to be referred to by the specified pronoun
        assertThat(string, equalTo("A dog gives a ball to her."))
        assertThat(
                "Object of Preposition should be in narrative context",
                narrativeContext.isKnownEntity(objectOfPreposition),
                equalTo(true)
        )
    }

}
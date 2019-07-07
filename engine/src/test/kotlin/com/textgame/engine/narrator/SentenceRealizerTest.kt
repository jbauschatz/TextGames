package com.textgame.engine.narrator

import com.textgame.engine.model.Person
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.MultipleVerbalClauses
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.sentence.VerbalClause
import com.textgame.engine.model.verb.Verb
import com.textgame.engine.test.TestNamedEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SentenceRealizerTest {

    private lateinit var narrativeContext: NarrativeContext
    private lateinit var sentenceRealizer: SentenceRealizer

    /**
     * Initializes each test case with a new [SentenceRealizer] and an empty [NarrativeContext]
     */
    @BeforeEach
    fun beforeEach() {
        narrativeContext = NarrativeContext()
        sentenceRealizer = SentenceRealizer(narrativeContext)
    }

    @Test
    fun realize_properSubjectAndObject() {
        // GIVEN a Sentence whose Subject and Object are Proper Nouns
        val sentence = SimpleSentence(
                TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE),
                Verb("sees", "see"),
                TestNamedEntity(2, ProperNoun("Jill"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the Subject and Direct Object to be referenced by their proper names
        assertThat(string, equalTo("Jack sees Jill."))
    }

    @Test
    fun realize_unknownNouns() {
        // GIVEN a Sentence whose Subject and Object are Nouns, and unknown in the Narrative context
        val subject = TestNamedEntity(1, Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val directObject = TestNamedEntity(2, Noun("ball"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)

        val sentence = SimpleSentence(
                subject,
                Verb("chases", "chase"),
                directObject
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the Subject and Direct Object to be indefinite, and now added to the Narrative Context
        assertThat(string, equalTo("A dog chases a ball."))
        assertThat("Subject should be in narrative context", narrativeContext.isKnownEntity(subject), equalTo(true))
        assertThat("DirectObject should be in narrative context", narrativeContext.isKnownEntity(directObject), equalTo(true))
    }

    @Test
    fun realize_knownNouns() {
        // GIVEN a Sentence whose Subject and Object are Nouns known in the NarrativeContext
        val subject = TestNamedEntity(1, Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val directObject = TestNamedEntity(2, Noun("ball"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)

        narrativeContext.addKnownEntity(subject)
        narrativeContext.addKnownEntity(directObject)

        val sentence = SimpleSentence(
                subject,
                Verb("chases", "chase"),
                directObject
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the Subject and Direct Object to be definite
        assertThat(string, equalTo("The dog chases the ball."))

        assertThat("Subject should be in narrative context", narrativeContext.isKnownEntity(subject), equalTo(true))
        assertThat("DirectObject should be in narrative context", narrativeContext.isKnownEntity(directObject), equalTo(true))
    }

    @Test
    fun realize_reflexive() {
        // GIVEN a Sentence whose Subject and Object are the same entity
        val subjectObject = TestNamedEntity(1, Noun("girl"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)

        val sentence = SimpleSentence(
                subjectObject,
                Verb("hurts", "hurt"),
                subjectObject
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the Direct Object to be the reflexive pronoun
        assertThat(string, equalTo("A girl hurts herself."))

        assertThat("Subject should be in narrative context", narrativeContext.isKnownEntity(subjectObject), equalTo(true))
    }

    @Test
    fun realize_pronounOverride() {
        // GIVEN a Sentence whose Subject has an overridden pronoun
        val subject = TestNamedEntity(1, Noun("boy"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        sentenceRealizer.overridePerson(subject, Person.SECOND)

        val sentence = SimpleSentence(
                subject,
                Verb("draws", "draw"),
                TestNamedEntity(2, Noun("sword"), Pronouns.THIRD_PERSON_PLURAL_NEUTER)
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the Subject to use the overridden pronouns
        assertThat(string, equalTo("You draw a sword."))
    }

    @Test
    fun realize_reflexiveWithOverride() {
        // GIVEN a Sentence whose Subject and Object are the same entity
        val subjectObject = TestNamedEntity(1, Noun("girl"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        sentenceRealizer.overridePerson(subjectObject, Person.SECOND)

        val sentence = SimpleSentence(
                subjectObject,
                Verb("hurts", "hurt"),
                subjectObject
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the Subject and Direct Object to use the specified pronouns, and the DO to be reflexive
        assertThat(string, equalTo("You hurt yourself."))
    }

    @Test
    fun realize_prepositionalPhrase() {
        // GIVEN a Sentence with a Subject, Object, and Prepositional Phrase
        val subject = TestNamedEntity(1, Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        val directObject = TestNamedEntity(2, Noun("ball"), Pronouns.THIRD_PERSON_PLURAL_NEUTER)
        val objectOfPreposition = TestNamedEntity(3, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)

        val sentence = SimpleSentence(
                subject,
                Verb("gives", "give"),
                directObject,
                PrepositionalPhrase("to", objectOfPreposition)
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the prepositional phrase to be included at the end of the sentence
        assertThat(string, equalTo("A dog gives a ball to Jack."))
        assertThat(
                "Object of Preposition should be in narrative context",
                narrativeContext.isKnownEntity(objectOfPreposition),
                equalTo(true)
        )
    }

    @Test
    fun realize_prepositionalPhrase_override() {
        // GIVEN a Sentence with a Subject, Object, and Prepositional Phrase whose subject has overridden pronouns
        val subject = TestNamedEntity(1, Noun("dog"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        val directObject = TestNamedEntity(2, Noun("ball"), Pronouns.THIRD_PERSON_PLURAL_NEUTER)
        val objectOfPreposition = TestNamedEntity(3, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)

        sentenceRealizer.overridePerson(objectOfPreposition, Person.SECOND)

        val sentence = SimpleSentence(
                subject,
                Verb("gives", "give"),
                directObject,
                PrepositionalPhrase("to", objectOfPreposition)
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the object of the preposition to be referred to by the specified pronoun
        assertThat(string, equalTo("A dog gives a ball to you."))
        assertThat(
                "Object of Preposition should be in narrative context",
                narrativeContext.isKnownEntity(objectOfPreposition),
                equalTo(true)
        )
    }

    @Test
    fun realize_knownComplexNames() {
        // GIVEN a Sentence whose Subject and Direct Object are known and have complex names
        val subject = TestNamedEntity(1, Adjective("shaggy", Noun("dog")), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
        val directObject = TestNamedEntity(2, Adjective("red", Noun("ball")), Pronouns.THIRD_PERSON_PLURAL_NEUTER)

        narrativeContext.addKnownEntity(subject)
        narrativeContext.addKnownEntity(directObject)

        val sentence = SimpleSentence(
                subject,
                Verb("chases", "chase"),
                directObject
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the entities to be referred to by simplified, definite names
        assertThat(string, equalTo("The dog chases the ball."))
    }

    @Test
    fun realize_twoVerbalClauses() {
        // GIVEN a sentence with two verbal clauses
        val jack = TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val hill = TestNamedEntity(2, Noun("hill"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)

        val sentence = MultipleVerbalClauses(
                jack,
                listOf(
                        VerbalClause(Verb("runs", "run"), prepositionalPhrase = PrepositionalPhrase("up", hill)),
                        VerbalClause(Verb("fills", "fill"), TestNamedEntity(3, Adjective("water", Noun("pail")), Pronouns.THIRD_PERSON_SINGULAR_NEUTER))
                )
        )
        narrativeContext.addKnownEntity(hill)

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the two sub-clauses to be joined together after the subject
        assertThat(string, equalTo("Jack runs up the hill and fills a water pail."))
    }

    @Test
    fun realize_threeVerbalClauses() {
        // GIVEN a sentence with three verbal clauses
        val jack = TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
        val hill = TestNamedEntity(2, Noun("hill"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
        val pail = TestNamedEntity(3, Noun("pail"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
        val water = TestNamedEntity(4, ProperNoun("water"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
        val jill = TestNamedEntity(5, ProperNoun("Jill"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)

        val sentence = MultipleVerbalClauses(
                jack,
                listOf(
                        VerbalClause(Verb("runs", "run"), prepositionalPhrase = PrepositionalPhrase("up", hill)),
                        VerbalClause(Verb("fills", "fills"), pail, PrepositionalPhrase("with", water)),
                        VerbalClause(Verb("gives", "give"), pail, PrepositionalPhrase("to", jill))
                )
        )
        narrativeContext.addKnownEntity(hill)

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the three sub-clauses to be joined together by commas after the subject
        assertThat(string, equalTo("Jack runs up the hill, fills a pail with water, and gives the pail to Jill."))
    }

}
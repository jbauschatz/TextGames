package com.textgame.engine.narrator

import com.textgame.engine.model.Person
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_PLURAL_NEUTER
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_FEMININE
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_MASCULINE
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_NEUTER
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.predicate.VerbMultipleObjects
import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.predicate.Predicates
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.verb.Verb
import com.textgame.engine.test.TestNamedEntity
import com.textgame.test.JACK
import com.textgame.test.JILL
import com.textgame.test.SEE
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
                JACK,
                VerbPredicate(SEE, JILL)
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the Subject and Direct Object to be referenced by their proper names
        assertThat(string, equalTo("Jack sees Jill."))
    }

    @Test
    fun realize_unknownNouns() {
        // GIVEN a Sentence whose Subject and Object are Nouns, and unknown in the Narrative context
        val subject = TestNamedEntity(1, Noun("dog"), THIRD_PERSON_SINGULAR_MASCULINE)
        val directObject = TestNamedEntity(2, Noun("ball"), THIRD_PERSON_SINGULAR_FEMININE)

        val sentence = SimpleSentence(
                subject,
                VerbPredicate(
                        Verb("chases", "chase"),
                        directObject
                )
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
        val subject = TestNamedEntity(1, Noun("dog"), THIRD_PERSON_SINGULAR_MASCULINE)
        val directObject = TestNamedEntity(2, Noun("ball"), THIRD_PERSON_SINGULAR_FEMININE)

        narrativeContext.addKnownEntity(subject)
        narrativeContext.addKnownEntity(directObject)

        val sentence = SimpleSentence(
                subject,
                VerbPredicate(
                        Verb("chases", "chase"),
                        directObject
                )
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
        val subjectObject = TestNamedEntity(1, Noun("girl"), THIRD_PERSON_SINGULAR_FEMININE)

        val sentence = SimpleSentence(
                subjectObject,
                VerbPredicate(
                        Verb("hurts", "hurt"),
                        subjectObject
                )
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
        val subject = TestNamedEntity(1, Noun("boy"), THIRD_PERSON_SINGULAR_MASCULINE)
        sentenceRealizer.overridePerson(subject, Person.SECOND)

        val sentence = SimpleSentence(
                subject,
                VerbPredicate(
                        Verb("draws", "draw"),
                        TestNamedEntity(2, Noun("sword"), THIRD_PERSON_PLURAL_NEUTER)
                )
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the Subject to use the overridden pronouns
        assertThat(string, equalTo("You draw a sword."))
    }

    @Test
    fun realize_reflexiveWithOverride() {
        // GIVEN a Sentence whose Subject and Object are the same entity
        val subjectObject = TestNamedEntity(1, Noun("girl"), THIRD_PERSON_SINGULAR_FEMININE)
        sentenceRealizer.overridePerson(subjectObject, Person.SECOND)

        val sentence = SimpleSentence(
                subjectObject,
                VerbPredicate(
                        Verb("hurts", "hurt"),
                        subjectObject
                )
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the Subject and Direct Object to use the specified pronouns, and the DO to be reflexive
        assertThat(string, equalTo("You hurt yourself."))
    }

    @Test
    fun realize_prepositionalPhrase() {
        // GIVEN a Sentence with a Subject, Object, and Prepositional Phrase
        val subject = TestNamedEntity(1, Noun("dog"), THIRD_PERSON_SINGULAR_FEMININE)
        val directObject = TestNamedEntity(2, Noun("ball"), THIRD_PERSON_PLURAL_NEUTER)
        val objectOfPreposition = TestNamedEntity(3, ProperNoun("Jack"), THIRD_PERSON_SINGULAR_MASCULINE)

        val sentence = SimpleSentence(
                subject,
                VerbPredicate(
                        Verb("gives", "give"),
                        directObject,
                        PrepositionalPhrase("to", objectOfPreposition)
                )
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
        val subject = TestNamedEntity(1, Noun("dog"), THIRD_PERSON_SINGULAR_FEMININE)
        val directObject = TestNamedEntity(2, Noun("ball"), THIRD_PERSON_PLURAL_NEUTER)
        val objectOfPreposition = TestNamedEntity(3, ProperNoun("Jack"), THIRD_PERSON_SINGULAR_MASCULINE)

        sentenceRealizer.overridePerson(objectOfPreposition, Person.SECOND)

        val sentence = SimpleSentence(
                subject,
                VerbPredicate(
                        Verb("gives", "give"),
                        directObject,
                        PrepositionalPhrase("to", objectOfPreposition)
                )
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
        val subject = TestNamedEntity(1, Adjective("shaggy", Noun("dog")), THIRD_PERSON_SINGULAR_FEMININE)
        val directObject = TestNamedEntity(2, Adjective("red", Noun("ball")), THIRD_PERSON_PLURAL_NEUTER)

        narrativeContext.addKnownEntity(subject)
        narrativeContext.addKnownEntity(directObject)

        val sentence = SimpleSentence(
                subject,
                VerbPredicate(
                        Verb("chases", "chase"),
                        directObject
                )
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the entities to be referred to by simplified, definite names
        assertThat(string, equalTo("The dog chases the ball."))
    }

    @Test
    fun realize_twoVerbalClauses() {
        // GIVEN a sentence with two verbal clauses
        val jack = TestNamedEntity(1, ProperNoun("Jack"), THIRD_PERSON_SINGULAR_MASCULINE)
        val hill = TestNamedEntity(2, Noun("hill"), THIRD_PERSON_SINGULAR_NEUTER)

        val sentence = SimpleSentence(
                jack,
                Predicates(listOf(
                        VerbPredicate(Verb("runs", "run"), prepositionalPhrase = PrepositionalPhrase("up", hill)),
                        VerbPredicate(Verb("fills", "fill"), TestNamedEntity(3, Adjective("water", Noun("pail")), THIRD_PERSON_SINGULAR_NEUTER))
                ))
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
        val jack = TestNamedEntity(1, ProperNoun("Jack"), THIRD_PERSON_SINGULAR_MASCULINE)
        val hill = TestNamedEntity(2, Noun("hill"), THIRD_PERSON_SINGULAR_NEUTER)
        val pail = TestNamedEntity(3, Noun("pail"), THIRD_PERSON_SINGULAR_NEUTER)
        val water = TestNamedEntity(4, ProperNoun("water"), THIRD_PERSON_SINGULAR_NEUTER)
        val jill = TestNamedEntity(5, ProperNoun("Jill"), THIRD_PERSON_SINGULAR_FEMININE)

        val sentence = SimpleSentence(
                jack,
                Predicates(listOf(
                        VerbPredicate(Verb("runs", "run"), prepositionalPhrase = PrepositionalPhrase("up", hill)),
                        VerbPredicate(Verb("fills", "fill"), pail, PrepositionalPhrase("with", water)),
                        VerbPredicate(Verb("gives", "give"), pail, PrepositionalPhrase("to", jill))
                ))
        )
        narrativeContext.addKnownEntity(hill)

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the three sub-clauses to be joined together by commas after the subject
        assertThat(string, equalTo("Jack runs up the hill, fills a pail with water, and gives the pail to Jill."))
    }

    @Test
    fun realize_verbWithTwoObjects() {
        // GIVEN a sentence with a verb and two direct objects
        val sentence = SimpleSentence(
                TestNamedEntity(1, Noun("girl"), THIRD_PERSON_SINGULAR_FEMININE),
                VerbMultipleObjects(
                        Verb("eats", "eat"),
                        listOf(
                                TestNamedEntity(2, Noun("steak"), THIRD_PERSON_SINGULAR_NEUTER),
                                TestNamedEntity(3, Noun("potato"), THIRD_PERSON_SINGULAR_NEUTER)
                        )
                )
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT both objects to be listed after the verb
        assertThat(string, equalTo("A girl eats a steak and a potato."))
    }

    @Test
    fun realize_verbWithThreeObjects() {
        // GIVEN a sentence with a verb and three direct objects
        val sentence = SimpleSentence(
                TestNamedEntity(1, Noun("girl"), THIRD_PERSON_SINGULAR_FEMININE),
                VerbMultipleObjects(
                        Verb("eats", "eat"),
                        listOf(
                                TestNamedEntity(2, Noun("steak"), THIRD_PERSON_SINGULAR_NEUTER),
                                TestNamedEntity(3, Noun("potato"), THIRD_PERSON_SINGULAR_NEUTER),
                                TestNamedEntity(4, Noun("salad"), THIRD_PERSON_SINGULAR_NEUTER)
                        )
                )
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT all three objects to be listed after the verb
        assertThat(string, equalTo("A girl eats a steak, a potato, and a salad."))
    }

    @Test
    fun realize_pronoun_unambiguous() {
        // GIVEN a sentence which repeats the same object twice
        val dog = TestNamedEntity(1, Noun("dog"), THIRD_PERSON_SINGULAR_MASCULINE)
        val bone = TestNamedEntity(2, Noun("bone"), THIRD_PERSON_SINGULAR_NEUTER)

        val sentence = SimpleSentence(
                dog,
                Predicates(listOf(
                        VerbPredicate(Verb("finds", "find"), bone),
                        VerbPredicate(Verb("eats", "eat"), bone)
                ))
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT bone to be referred to by its pronoun the second time
        assertThat(string, equalTo("A dog finds a bone and eats it."))
    }

    @Test
    fun realize_possessiveDeterminer_unknownEntity() {
        // GIVEN a sentence containing an owning entity and an owned one, which is not known
        val dog = TestNamedEntity(1, Noun("dog"), THIRD_PERSON_SINGULAR_MASCULINE)
        val bone = TestNamedEntity(2, Noun("bone"), THIRD_PERSON_SINGULAR_NEUTER)
        bone.addOwner(dog)

        val sentence = SimpleSentence(
                dog,
                VerbPredicate(
                        Verb("eats", "eat"),
                        bone
                )
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the possessive determiner NOT to be used with the owned entity
        assertThat(string, equalTo("A dog eats a bone."))
    }

    @Test
    fun realize_possessiveDeterminer() {
        // GIVEN a sentence containing an owning entity and an owned one, which is known
        val dog = TestNamedEntity(1, Noun("dog"), THIRD_PERSON_SINGULAR_MASCULINE)
        val bone = TestNamedEntity(2, Noun("bone"), THIRD_PERSON_SINGULAR_NEUTER)
        bone.addOwner(dog)
        narrativeContext.addKnownEntity(bone)

        val sentence = SimpleSentence(
                dog,
                VerbPredicate(
                        Verb("eats", "eat"),
                        bone
                )
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the possessive determiner to be used with the owned entity
        assertThat(string, equalTo("A dog eats his bone."))
    }

    @Test
    fun realize_possessiveDeterminer_mixedWithPronoun() {
        // GIVEN a sentence referencing an owned entity multiple times
        val dog = TestNamedEntity(1, Noun("dog"), THIRD_PERSON_SINGULAR_MASCULINE)
        val bone = TestNamedEntity(2, Noun("bone"), THIRD_PERSON_SINGULAR_NEUTER)
        bone.addOwner(dog)

        narrativeContext.addKnownEntity(dog)
        narrativeContext.addKnownEntity(bone)

        val sentence = SimpleSentence(
                dog,
                Predicates(listOf(
                        VerbPredicate(Verb("finds", "find"), bone),
                        VerbPredicate(Verb("eats", "eat"), bone)
                ))
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the possessive determiner to be used first, followed by the pronoun
        assertThat(string, equalTo("The dog finds his bone and eats it."))
    }

    @Test
    fun realize_possessiveDeterminer_withinPreposition() {
        // GIVEN a sentence referencing an owned entity within a prepositional phrase
        val cat = TestNamedEntity(1, Noun("cat"), THIRD_PERSON_SINGULAR_FEMININE)
        val dog = TestNamedEntity(2, Noun("dog"), THIRD_PERSON_SINGULAR_MASCULINE)
        val paw = TestNamedEntity(3, Noun("paw"), THIRD_PERSON_SINGULAR_NEUTER)
        paw.addOwner(cat)

        narrativeContext.addKnownEntity(cat)
        narrativeContext.addKnownEntity(paw)

        val sentence = SimpleSentence(
                cat,
                VerbPredicate(
                        Verb("swats", "swat"),
                        dog,
                        PrepositionalPhrase("with", paw)
                )
        )

        // WHEN realizing the sentence
        val string = sentenceRealizer.realize(sentence)

        // EXPECT the possessive determiner to be used within the prepositional phrase
        assertThat(string, equalTo("The cat swats a dog with her paw."))
    }

}
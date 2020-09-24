package com.textgame.engine.narrator

import com.textgame.engine.model.GrammaticalPerson
import com.textgame.engine.model.nounphrase.*
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_PLURAL_NEUTER
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_FEMININE
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_MASCULINE
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_NEUTER
import com.textgame.engine.model.predicate.Predicates
import com.textgame.engine.model.predicate.VerbMultipleObjects
import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.verb.Verb
import com.textgame.engine.test.TestNamedEntity
import com.textgame.test.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
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
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the Subject and Direct Object to be referenced by their proper names
        assertThat(elements, contains(
                NameElement(ProperNoun("Jack"), JACK),
                TextElement("sees"),
                NameElement(ProperNoun("Jill"), JILL),
                PunctuationElement(".")
        ))
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
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the Subject and Direct Object to be indefinite, and now added to the Narrative Context
        assertThat(elements, contains(
                NameElement(Indefinite(Noun("dog")), subject),
                TextElement("chases"),
                NameElement(Indefinite(Noun("ball")), directObject),
                PunctuationElement(".")
        ))
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
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the Subject and Direct Object to be definite
        assertThat(elements, contains(
                NameElement(Definite(Noun("dog")), subject),
                TextElement("chases"),
                NameElement(Definite(Noun("ball")), directObject),
                PunctuationElement(".")
        ))

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
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the Direct Object to be the reflexive pronoun
        assertThat(elements, contains(
                NameElement(Indefinite(Noun("girl")), subjectObject),
                TextElement("hurts"),
                NameElement(Pronoun("herself"), subjectObject),
                PunctuationElement(".")
        ))

        assertThat("Subject should be in narrative context", narrativeContext.isKnownEntity(subjectObject), equalTo(true))
    }

    @Test
    fun realize_pronounOverride() {
        // GIVEN a Sentence whose Subject has an overridden pronoun
        val subject = TestNamedEntity(1, Noun("boy"), THIRD_PERSON_SINGULAR_MASCULINE)
        sentenceRealizer.overridePerson(subject, GrammaticalPerson.SECOND)

        val directObject = TestNamedEntity(2, Noun("sword"), THIRD_PERSON_PLURAL_NEUTER)

        val sentence = SimpleSentence(
                subject,
                VerbPredicate(
                        Verb("draws", "draw"),
                        directObject
                )
        )

        // WHEN realizing the sentence
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the Subject to use the overridden pronouns
        assertThat(elements, contains(
                NameElement(Pronoun("you"), subject),
                TextElement("draw"),
                NameElement(Indefinite(Noun("sword")), directObject),
                PunctuationElement(".")
        ))
    }

    @Test
    fun realize_reflexiveWithOverride() {
        // GIVEN a Sentence whose Subject and Object are the same entity
        val subjectObject = TestNamedEntity(1, Noun("girl"), THIRD_PERSON_SINGULAR_FEMININE)
        sentenceRealizer.overridePerson(subjectObject, GrammaticalPerson.SECOND)

        val sentence = SimpleSentence(
                subjectObject,
                VerbPredicate(
                        Verb("hurts", "hurt"),
                        subjectObject
                )
        )

        // WHEN realizing the sentence
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the Subject and Direct Object to use the specified pronouns, and the DO to be reflexive
        assertThat(elements, contains(
                NameElement(Pronoun("you"), subjectObject),
                TextElement("hurt"),
                NameElement(Pronoun("yourself"), subjectObject),
                PunctuationElement(".")
        ))
    }

    @Test
    fun realize_prepositionalPhrase() {
        // GIVEN a Sentence with a Subject, Object, and Prepositional Phrase
        val subject = TestNamedEntity(-1, Noun("dog"), THIRD_PERSON_SINGULAR_FEMININE)
        val directObject = TestNamedEntity(-2, Noun("ball"), THIRD_PERSON_PLURAL_NEUTER)

        val sentence = SimpleSentence(
                subject,
                VerbPredicate(
                        Verb("gives", "give"),
                        directObject,
                        PrepositionalPhrase("to", JACK)
                )
        )

        // WHEN realizing the sentence
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the prepositional phrase to be included at the end of the sentence
        assertThat(elements, contains(
                NameElement(Indefinite(Noun("dog")), subject),
                TextElement("gives"),
                NameElement(Indefinite(Noun("ball")), directObject),
                TextElement("to"),
                NameElement(ProperNoun("Jack"), JACK),
                PunctuationElement(".")
        ))
        assertThat(
                "Object of Preposition should be in narrative context",
                narrativeContext.isKnownEntity(JACK),
                equalTo(true)
        )
    }

    @Test
    fun realize_prepositionalPhrase_override() {
        // GIVEN a Sentence with a Subject, Object, and Prepositional Phrase whose subject has overridden pronouns
        val subject = TestNamedEntity(1, Noun("dog"), THIRD_PERSON_SINGULAR_FEMININE)
        val directObject = TestNamedEntity(2, Noun("ball"), THIRD_PERSON_PLURAL_NEUTER)
        val objectOfPreposition = TestNamedEntity(3, ProperNoun("Jack"), THIRD_PERSON_SINGULAR_MASCULINE)

        sentenceRealizer.overridePerson(objectOfPreposition, GrammaticalPerson.SECOND)

        val sentence = SimpleSentence(
                subject,
                VerbPredicate(
                        Verb("gives", "give"),
                        directObject,
                        PrepositionalPhrase("to", objectOfPreposition)
                )
        )

        // WHEN realizing the sentence
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the object of the preposition to be referred to by the specified pronoun
        assertThat(elements, contains(
                NameElement(Indefinite(Noun("dog")), subject),
                TextElement("gives"),
                NameElement(Indefinite(Noun("ball")), directObject),
                TextElement("to"),
                NameElement(Pronoun("you"), objectOfPreposition),
                PunctuationElement(".")
        ))
        assertThat(
                "Object of Preposition should be in narrative context",
                narrativeContext.isKnownEntity(objectOfPreposition),
                equalTo(true)
        )
    }

    /**
     * The object of a preposition should not influence pronoun usage.
     * If the same noun is used directly before and after a prepositional phrase, the noun's pronoun should be used
     * the second time (ie, "skip" the object of the preposition)
     */
    @Test
    fun realize_repeatPronounAfterPrepositionalPhrase() {
        // GIVEN a sentence with a noun occurring before and after a prepositional phrase
        val sentence = SimpleSentence(
                JACK,
                Predicates(listOf(
                        VerbPredicate(SEE, APPLE, prepositionalPhrase = PrepositionalPhrase("on", HILL)),
                        VerbPredicate(EAT, APPLE)
                ))
        )

        // WHEN realizing the sentence
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the noun's pronoun to be used after the prepositional phrase
        assertThat(elements, contains(
                NameElement(ProperNoun("Jack"), JACK),
                TextElement("sees"),
                NameElement(Indefinite(Noun("apple")), APPLE),
                TextElement("on"),
                NameElement(Indefinite(Noun("hill")), HILL),
                PunctuationElement(","),
                TextElement("and"),
                TextElement("eats"),
                NameElement(Pronoun("it"), APPLE),
                PunctuationElement(".")
        ))
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
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the entities to be referred to by simplified, definite names
        assertThat(elements, contains(
                NameElement(Definite(Noun("dog")), subject),
                TextElement("chases"),
                NameElement(Definite(Noun("ball")), directObject),
                PunctuationElement(".")
        ))
    }

    @Test
    fun realize_twoVerbalClauses() {
        // GIVEN a sentence with two verbal clauses
        val waterPail = TestNamedEntity(-1, Adjective("water", Noun("pail")), THIRD_PERSON_SINGULAR_NEUTER)

        val sentence = SimpleSentence(
                JACK,
                Predicates(listOf(
                        VerbPredicate(Verb("runs", "run"), prepositionalPhrase = PrepositionalPhrase("up", HILL)),
                        VerbPredicate(Verb("fills", "fill"), waterPail)
                ))
        )
        narrativeContext.addKnownEntity(HILL)

        // WHEN realizing the sentence
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the two sub-clauses to be joined together after the subject
        assertThat(elements, contains(
                NameElement(ProperNoun("Jack"), JACK),
                TextElement("runs"),
                TextElement("up"),
                NameElement(Definite(Noun("hill")), HILL),
                PunctuationElement(","),
                TextElement("and"),
                TextElement("fills"),
                NameElement(Indefinite(Adjective("water", Noun("pail"))), waterPail),
                PunctuationElement(".")
        ))
    }

    @Test
    fun realize_threeVerbalClauses() {
        // GIVEN a sentence with three verbal clauses
        val sentence = SimpleSentence(
                JACK,
                Predicates(listOf(
                        VerbPredicate(GO, prepositionalPhrase = PrepositionalPhrase("up", HILL)),
                        VerbPredicate(Verb("fills", "fill"), PAIL, PrepositionalPhrase("with", WATER)),
                        VerbPredicate(Verb("gives", "give"), PAIL, PrepositionalPhrase("to", JILL))
                ))
        )
        narrativeContext.addKnownEntity(HILL)

        // WHEN realizing the sentence
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the three sub-clauses to be joined together by commas after the subject
        assertThat(elements, contains(
                NameElement(ProperNoun("Jack"), JACK),
                TextElement("goes"),
                TextElement("up"),
                NameElement(Definite(Noun("hill")), HILL),
                PunctuationElement(","),
                TextElement("fills"),
                NameElement(Indefinite(Noun("pail")), PAIL),
                TextElement("with"),
                NameElement(ProperNoun("water"), WATER),
                PunctuationElement(","),
                TextElement("and"),
                TextElement("gives"),
                NameElement(Pronoun("it"), PAIL),
                TextElement("to"),
                NameElement(ProperNoun("Jill"), JILL),
                PunctuationElement(".")
        ))
    }

    @Test
    fun realize_verbWithTwoObjects() {
        // GIVEN a sentence with a verb and two direct objects
        val girl = TestNamedEntity(1, Noun("girl"), THIRD_PERSON_SINGULAR_FEMININE)
        val steak = TestNamedEntity(2, Noun("steak"), THIRD_PERSON_SINGULAR_NEUTER)
        val potato = TestNamedEntity(3, Noun("potato"), THIRD_PERSON_SINGULAR_NEUTER)

        val sentence = SimpleSentence(
                girl,
                VerbMultipleObjects(
                        Verb("eats", "eat"),
                        listOf(steak, potato)
                )
        )

        // WHEN realizing the sentence
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT both objects to be listed after the verb
        assertThat(elements, contains(
                NameElement(Indefinite(Noun("girl")), girl),
                TextElement("eats"),
                NameElement(Indefinite(Noun("steak")), steak),
                TextElement("and"),
                NameElement(Indefinite(Noun("potato")), potato),
                PunctuationElement(".")
        ))
    }

    @Test
    fun realize_verbWithThreeObjects() {
        // GIVEN a sentence with a verb and three direct objects
        val girl = TestNamedEntity(1, Noun("girl"), THIRD_PERSON_SINGULAR_FEMININE)
        val steak = TestNamedEntity(2, Noun("steak"), THIRD_PERSON_SINGULAR_NEUTER)
        val potato = TestNamedEntity(3, Noun("potato"), THIRD_PERSON_SINGULAR_NEUTER)
        val salad = TestNamedEntity(4, Noun("salad"), THIRD_PERSON_SINGULAR_NEUTER)

        val sentence = SimpleSentence(
                girl,
                VerbMultipleObjects(
                        Verb("eats", "eat"),
                        listOf(steak, potato, salad)
                )
        )

        // WHEN realizing the sentence
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT all three objects to be listed after the verb
        assertThat(elements, contains(
                NameElement(Indefinite(Noun("girl")), girl),
                TextElement("eats"),
                NameElement(Indefinite(Noun("steak")), steak),
                PunctuationElement(","),
                NameElement(Indefinite(Noun("potato")), potato),
                PunctuationElement(","),
                TextElement("and"),
                NameElement(Indefinite(Noun("salad")), salad),
                PunctuationElement(".")
        ))
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
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT bone to be referred to by its pronoun the second time
        assertThat(elements, contains(
                NameElement(Indefinite(Noun("dog")), dog),
                TextElement("finds"),
                NameElement(Indefinite(Noun("bone")), bone),
                PunctuationElement(","),
                TextElement("and"),
                TextElement("eats"),
                NameElement(Pronoun("it"), bone),
                PunctuationElement(".")
        ))
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
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the possessive determiner NOT to be used with the owned entity
        assertThat(elements, contains(
                NameElement(Indefinite(Noun("dog")), dog),
                TextElement("eats"),
                NameElement(Indefinite(Noun("bone")), bone),
                PunctuationElement(".")
        ))
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
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the possessive determiner to be used with the owned entity
        assertThat(elements, contains(
                NameElement(Indefinite(Noun("dog")), dog),
                TextElement("eats"),
                NameElement(PossessivePronoun(Pronoun("his"), Noun("bone")), bone),
                PunctuationElement(".")
        ))
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
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the possessive determiner to be used first, followed by the pronoun
        assertThat(elements, contains(
                NameElement(Definite(Noun("dog")), dog),
                TextElement("finds"),
                NameElement(PossessivePronoun(Pronoun("his"), Noun("bone")), bone),
                PunctuationElement(","),
                TextElement("and"),
                TextElement("eats"),
                NameElement(Pronoun("it"), bone),
                PunctuationElement(".")
        ))
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
        val elements = sentenceRealizer.realize(sentence)

        // EXPECT the possessive determiner to be used within the prepositional phrase
        assertThat(elements, contains(
                NameElement(Definite(Noun("cat")), cat),
                TextElement("swats"),
                NameElement(Indefinite(Noun("dog")), dog),
                TextElement("with"),
                NameElement(PossessivePronoun(Pronoun("her"), Noun("paw")), paw),
                PunctuationElement(".")
        ))
    }

}
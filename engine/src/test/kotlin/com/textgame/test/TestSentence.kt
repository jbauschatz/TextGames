package com.textgame.test

import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.verb.Verb
import com.textgame.engine.test.TestNamedEntity

val JACK = TestNamedEntity(1, ProperNoun("Jack"), Pronouns.THIRD_PERSON_SINGULAR_MASCULINE)
val JILL = TestNamedEntity(2, ProperNoun("Jill"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE)
val HILL = TestNamedEntity(3, Noun("hill"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
val APPLE = TestNamedEntity(4, Noun("apple"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
val ORANGE = TestNamedEntity(6, Noun("orange"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
val COOKIE = TestNamedEntity(7, Noun("cookie"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
val WATER = TestNamedEntity(8, ProperNoun("water"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
val PAIL = TestNamedEntity(9, Noun("pail"), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)

val GO = Verb("goes", "go")
val EAT = Verb("eats", "eat")
val DRINK = Verb("drinks", "drink")
val SEE = Verb("sees", "see")

val JACK_GOES_UPTHEHILL = SimpleSentence(JACK, VerbPredicate(GO, prepositionalPhrase = PrepositionalPhrase("up", HILL)))

val JILL_GOES_UPTHEHILL = SimpleSentence(JILL, VerbPredicate(GO, prepositionalPhrase = PrepositionalPhrase("up", HILL)))

val JACK_EATS_APPLE = SimpleSentence(JACK, VerbPredicate(EAT, APPLE))

val JILL_EATS_APPLE = SimpleSentence(JILL, VerbPredicate(EAT, APPLE))

val JACK_EATS_COOKIE = SimpleSentence(JACK, VerbPredicate(EAT, COOKIE))

val JACK_EATS_ORANGE = SimpleSentence(JACK, VerbPredicate(EAT, ORANGE))

val JACK_DRINKS_WATER = SimpleSentence(JACK, VerbPredicate(DRINK, WATER))
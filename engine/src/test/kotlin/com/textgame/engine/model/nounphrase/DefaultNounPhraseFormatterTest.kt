package com.textgame.engine.model.nounphrase

import com.textgame.engine.format.DefaultNounPhraseFormatter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class DefaultNounPhraseFormatterTest {

    companion object {

        @JvmStatic
        fun format_arguments(): Stream<Arguments> = Stream.of(
                Arguments.of(ProperNoun("Jack"), true, false, "Jack"),
                Arguments.of(ProperNoun("Jack"), false, false, "Jack"),
                Arguments.of(Noun("dog"), true, false, "Dog"),
                Arguments.of(Noun("dog"), false, false, "dog"),

                Arguments.of(Definite(Noun("dog")), true, false, "The dog"),
                Arguments.of(Definite(Noun("dog")), false, false, "the dog"),
                Arguments.of(Definite(Noun("dog")), false, true, "the Dog"),
                Arguments.of(Definite(Noun("dog")), true, true, "The Dog"),

                Arguments.of(Indefinite(Noun("dog")), true, false, "A dog"),
                Arguments.of(Indefinite(Noun("dog")), false, false, "a dog"),
                Arguments.of(Indefinite(Noun("dog")), false, true, "a Dog"),
                Arguments.of(Indefinite(Noun("dog")), true, true, "A Dog"),
                Arguments.of(Indefinite(Noun("egg")), true, false, "An egg"),
                Arguments.of(Indefinite(Noun("egg")), false, false, "an egg"),
                Arguments.of(Indefinite(Noun("egg")), false, true, "an Egg"),
                Arguments.of(Indefinite(Noun("honor", true)), true, false, "An honor"),
                Arguments.of(Indefinite(Noun("honor", true)), false, false, "an honor"),

                Arguments.of(Pronoun("her"), true, false, "Her"),
                Arguments.of(Pronoun("her"), false, false, "her"),

                Arguments.of(Adjective("big", Noun("dog")), true, false, "Big dog"),
                Arguments.of(Adjective("big", Noun("dog")), false, false, "big dog"),
                Arguments.of(Adjective("big", Noun("dog")), false, true, "Big Dog")
        )
    }

    @ParameterizedTest(name = "{index} nounPhrase = {0}, capitalize = {1}, titleCase = {2}, should equal \"{3}\"")
    @MethodSource("format_arguments")
    fun format_parameterized(nounPhrase: NounPhrase, capitalize: Boolean, titleCase: Boolean, expected: String) {
        assertThat(
                "Formatted NounPhrase should equal \"$expected\"",
                DefaultNounPhraseFormatter.format(nounPhrase, null, capitalize, titleCase),
                equalTo(expected)
        )
    }
}
package com.textgame.engine.model.nounphrase

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class NounPhraseFormatterTest {

    companion object {

        @JvmStatic
        fun format_arguments(): Stream<Arguments> = Stream.of(
                Arguments.of(ProperNoun("Jack"), true, "Jack"),
                Arguments.of(ProperNoun("Jack"), false, "Jack"),
                Arguments.of(Noun("dog"), true, "Dog"),
                Arguments.of(Noun("dog"), false, "dog"),
                Arguments.of(Definite(Noun("dog")), true, "The dog"),
                Arguments.of(Definite(Noun("dog")), false, "the dog"),
                Arguments.of(Indefinite(Noun("dog")), true, "A dog"),
                Arguments.of(Indefinite(Noun("dog")), false, "a dog"),
                Arguments.of(Indefinite(Noun("egg")), true, "An egg"),
                Arguments.of(Indefinite(Noun("egg")), false, "an egg"),
                Arguments.of(Indefinite(Noun("honor", true)), true, "An honor"),
                Arguments.of(Indefinite(Noun("honor", true)), false, "an honor")
        )
    }

    @ParameterizedTest(name = "{index} nounPhrase = {0}, capitalize = {1}, should equal \"{2}\"")
    @MethodSource("format_arguments")
    fun format_parameterized(nounPhrase: NounPhrase, capitalize: Boolean, expected: String) {
        assertThat(
                "Formatted NounPhrase should equal \"$expected\"",
                NounPhraseFormatter.format(nounPhrase, capitalize),
                equalTo(expected)
        )
    }
}
package com.textgame.engine.model.nounphrase

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PartialNameMatcherTest {

    companion object {

        @JvmStatic
        fun matches_arguments(): Stream<Arguments> = Stream.of(
                Arguments.of("Jack", ProperNoun("Jack"), true),
                Arguments.of("jack", ProperNoun("Jack"), true),
                Arguments.of("Jill", ProperNoun("Jack"), false),

                Arguments.of("key", Noun("key"), true),
                Arguments.of("KEY", Noun("key"), true),
                Arguments.of("spoon", Noun("key"), false),

                Arguments.of("gold coin", Adjective("gold", Noun("coin")), true),
                Arguments.of("Gold Coin", Adjective("gold", Noun("coin")), true),
                Arguments.of("coin", Adjective("gold", Noun("coin")), true),
                Arguments.of("Coin", Adjective("gold", Noun("coin")), true),
                Arguments.of("spoon", Adjective("gold", Noun("coin")), false)
        )
    }

    @ParameterizedTest(name = "{index} name = {0}, nounPhrase = {1}, expect match = {2}")
    @MethodSource("matches_arguments")
    fun matches_parameterized(name: String, nounPhrase: NounPhrase, expectMatch: Boolean) {
        val matchString = if (expectMatch) "match" else "not match"
        MatcherAssert.assertThat(
                "Name \"$name\" should $matchString nounPhrase: $nounPhrase",
                PartialNameMatcher.matches(name, nounPhrase),
                equalTo(expectMatch)
        )
    }
}
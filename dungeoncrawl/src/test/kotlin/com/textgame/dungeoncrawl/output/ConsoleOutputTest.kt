package com.textgame.dungeoncrawl.output

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ConsoleOutputTest {

    companion object {

        @JvmStatic
        fun length_arguments(): Stream<Arguments> = Stream.of(
                Arguments.of("", 0),
                Arguments.of("FOO", 3),
                Arguments.of("\u001b[31mFOO", 3),
                Arguments.of("\u001b[31;1mFOO", 3),
                Arguments.of("\u001B[31m\u001b[43mFOO", 3),

                Arguments.of("FOO\u001b[0m", 3),

                Arguments.of("\u001B[31mFOO\u001b[0m", 3),
                Arguments.of("\u001B[31m\u001B[43mFOO\u001b[0m", 3),

                Arguments.of("\u001B[31mFOO\u001b[43mBAR\u001B[0m", 6)
        )
    }

    @ParameterizedTest(name = "{index} length of {0}\u001B[0m should equal {1}")
    @MethodSource("length_arguments")
    fun length_parameterized(input: String, expectedLength: Int) {
        val output = ConsoleOutput(ConsoleNounPhraseFormatter, 80)

        assertThat(output.length(input), equalTo(expectedLength))
    }

}
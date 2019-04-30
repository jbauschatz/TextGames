package com.textgame.engine.model.nounphrase

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class NounPhraseFormatterTest {

    @Test
    fun format_ProperNoun_uncapitalized() {
        assertThat(
                NounPhraseFormatter.format(ProperNoun("Jack"), false),
                equalTo("Jack")
        )
    }

    @Test
    fun format_ProperNoun_capitalized() {
        assertThat(
                NounPhraseFormatter.format(ProperNoun("Jack"), true),
                equalTo("Jack")
        )
    }
}
package com.textgame.engine.model.nounphrase

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class NounPhraseFormatterTest {

    @Test
    fun format_Noun_uncapitalized() {
        assertThat(
                NounPhraseFormatter.format(Noun("dog"), false),
                equalTo("dog")
        )
    }

    @Test
    fun format_Noun_capitalized() {
        assertThat(
                NounPhraseFormatter.format(Noun("dog"), true),
                equalTo("Dog")
        )
    }

    @Test
    fun format_Indefinite_noVowelSound_uncapitalized() {
        assertThat(
                NounPhraseFormatter.format(Indefinite(Noun("dog")), false),
                equalTo("a dog")
        )
    }

    @Test
    fun format_Indefinite_noVowelSound_capitalized() {
        assertThat(
                NounPhraseFormatter.format(Indefinite(Noun("dog")), true),
                equalTo("A dog")
        )
    }

    @Test
    fun format_Indefinite_vowelSound_uncapitalized() {
        assertThat(
                NounPhraseFormatter.format(Indefinite(Noun("egg")), false),
                equalTo("an egg")
        )
    }

    @Test
    fun format_Indefinite_vowelSound_capitalized() {
        assertThat(
                NounPhraseFormatter.format(Indefinite(Noun("egg")), true),
                equalTo("An egg")
        )
    }

    @Test
    fun format_Indefinite_irregularVowelSound_capitalized() {
        assertThat(
                NounPhraseFormatter.format(Indefinite(Noun("honor", true)), true),
                equalTo("An honor")
        )
    }

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
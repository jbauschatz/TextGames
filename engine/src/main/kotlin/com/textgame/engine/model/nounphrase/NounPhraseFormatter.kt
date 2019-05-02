package com.textgame.engine.model.nounphrase

import java.lang.IllegalArgumentException

class NounPhraseFormatter {

    companion object {
        fun format(nounPhrase: NounPhrase, capitalize: Boolean = false): String =
                when(nounPhrase) {
                    is ProperNoun -> format(nounPhrase.value, capitalize)
                    is Noun -> format(nounPhrase.value, capitalize)
                    is Definite -> format("the ", capitalize) + format(nounPhrase.stem, false)
                    is Indefinite -> getArticle(nounPhrase, capitalize) + format(nounPhrase.stem, false)
                    is Pronoun -> format(nounPhrase.value, capitalize)
                    else -> throw IllegalArgumentException("Invalid Noun Phrase type: ${nounPhrase.javaClass}")
                }

        private fun format(string: String, capitalize: Boolean): String =
                if (capitalize) {
                    string[0].toUpperCase() + string.substring(1)
                } else {
                    string
                }

        private fun getArticle(indefinite: Indefinite, capitalize: Boolean) =
                format(
                        if (indefinite.stem.startsWithVowelSound()) "an "
                                else "a ",
                        capitalize
                )
    }
}
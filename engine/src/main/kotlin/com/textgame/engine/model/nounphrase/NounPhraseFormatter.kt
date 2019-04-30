package com.textgame.engine.model.nounphrase

import java.lang.IllegalArgumentException

class NounPhraseFormatter {

    companion object {
        fun format(nounPhrase: NounPhrase, capitalize: Boolean): String =
                when(nounPhrase) {
                    is ProperNoun -> format(nounPhrase.value, capitalize)
                    else -> throw IllegalArgumentException("Invalid Noun Phrase type: ${nounPhrase.javaClass}")
                }

        private fun format(string: String, capitalize: Boolean): String =
                if (capitalize) {
                    string[0].toUpperCase() + string.substring(1)
                } else {
                    string
                }
    }
}
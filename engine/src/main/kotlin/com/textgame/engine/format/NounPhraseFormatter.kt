package com.textgame.engine.format

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.*
import java.lang.IllegalArgumentException

interface NounPhraseFormatter {

    fun format(nounPhrase: NounPhrase, entity: NamedEntity? = null, capitalize: Boolean = false, titleCase: Boolean = false): String

}

object DefaultNounPhraseFormatter : NounPhraseFormatter {
    override fun format(nounPhrase: NounPhrase, entity: NamedEntity?, capitalize: Boolean, titleCase: Boolean): String =
            when(nounPhrase) {
                is ProperNoun -> formatMajorWord(nounPhrase.value, capitalize, titleCase)
                is Noun -> formatMajorWord(nounPhrase.value, capitalize, titleCase)
                is Definite ->
                    formatMinorWord("the ", capitalize) +
                            format(nounPhrase.stem, entity, false, titleCase)
                is Indefinite ->
                    getArticle(nounPhrase, capitalize) +
                        format(nounPhrase.stem, entity, false, titleCase)
                is Pronoun -> formatMajorWord(nounPhrase.value, capitalize, titleCase)
                is PossessivePronoun ->
                    formatMajorWord(nounPhrase.pronoun.value, capitalize, titleCase) +
                            " " + format(nounPhrase.head, entity, false, titleCase)
                is Adjective ->
                    formatMajorWord(nounPhrase.value, capitalize, titleCase) +
                            " " + format(nounPhrase.stem, entity, false, titleCase)
                else -> throw IllegalArgumentException("Invalid Noun Phrase type: ${nounPhrase.javaClass}")
            }

    private fun formatMajorWord(string: String, capitalize: Boolean, titleCaps: Boolean): String =
            if (titleCaps || capitalize)
                capitalizeString(string)
            else string

    private fun formatMinorWord(string: String, capitalize: Boolean): String =
            if (capitalize)
                capitalizeString(string)
            else
                string

    private fun getArticle(indefinite: Indefinite, capitalize: Boolean): String =
            formatMinorWord(
                    if (indefinite.stem.startsWithVowelSound()) "an "
                    else "a ",
                    capitalize
            )

    private fun capitalizeString(string: String) =
            string[0].toUpperCase() + string.substring(1)
}
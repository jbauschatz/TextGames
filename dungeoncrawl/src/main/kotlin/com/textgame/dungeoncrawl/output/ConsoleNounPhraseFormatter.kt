package com.textgame.dungeoncrawl.output

import com.diogonunes.jcolor.Ansi.colorize
import com.diogonunes.jcolor.AnsiFormat
import com.diogonunes.jcolor.Attribute.*
import com.textgame.dungeoncrawl.view.*
import com.textgame.engine.format.NounPhraseFormatter
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.*

object ConsoleNounPhraseFormatter : NounPhraseFormatter {

    override fun format(nounPhrase: NounPhrase, entity: NamedEntity?, capitalize: Boolean, titleCase: Boolean): String =
        when (nounPhrase) {
            is ProperNoun -> highlight(formatMajorWord(nounPhrase.value, capitalize, titleCase), entity)
            is Noun -> highlight(formatMajorWord(nounPhrase.value, capitalize, titleCase), entity)
            is Definite ->
                formatMinorWord("the ", capitalize) +
                        highlight(formatNoHighlight(nounPhrase.stem, false, titleCase), entity)
            is Indefinite ->
                getArticle(nounPhrase, capitalize) +
                        highlight(formatNoHighlight(nounPhrase.stem, false, titleCase), entity)
            is Pronoun -> formatMajorWord(nounPhrase.value, capitalize, titleCase)
            is PossessivePronoun ->
                formatMinorWord(nounPhrase.pronoun.value, capitalize) +
                    " " +
                    highlight(
                        formatNoHighlight(nounPhrase.head, false, titleCase),
                        entity
                    )
            is Adjective ->
                highlight(
                        formatMajorWord(nounPhrase.value, capitalize, titleCase) +
                                " " + formatNoHighlight(nounPhrase.stem, false, titleCase),
                        entity
                )
            else -> throw IllegalArgumentException("Invalid Noun Phrase type: ${nounPhrase.javaClass}")
        }

    fun formatNoHighlight(nounPhrase: NounPhrase, capitalize: Boolean, titleCase: Boolean): String =
        when (nounPhrase) {
            is ProperNoun -> formatMajorWord(nounPhrase.value, capitalize, titleCase)
            is Noun -> formatMajorWord(nounPhrase.value, capitalize, titleCase)
            is Definite ->
                formatMinorWord("the ", capitalize) +
                        formatNoHighlight(nounPhrase.stem, false, titleCase)
            is Indefinite ->
                getArticle(nounPhrase, capitalize) +
                        formatNoHighlight(nounPhrase.stem, false, titleCase)
            is Pronoun -> formatMajorWord(nounPhrase.value, capitalize, titleCase)
            is Adjective ->
                formatMajorWord(nounPhrase.value, capitalize, titleCase) +
                        " " + formatNoHighlight(nounPhrase.stem, false, titleCase)
            else -> throw IllegalArgumentException("Invalid Noun Phrase type: ${nounPhrase.javaClass}")
        }

    private fun highlight(string: String, entity: NamedEntity?): String {
        if (entity == null) return string

        val format = when(entity) {
            is CreatureView -> AnsiFormat(RED_TEXT())
            is ItemView -> AnsiFormat(YELLOW_TEXT())
            is DoorView -> AnsiFormat(CYAN_TEXT())
            is LocationView -> AnsiFormat(CYAN_TEXT())
            is ContainerView -> AnsiFormat(BOLD())
            else -> AnsiFormat()
        }

        return colorize(string, format)
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
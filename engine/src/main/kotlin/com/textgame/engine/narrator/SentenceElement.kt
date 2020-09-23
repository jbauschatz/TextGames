package com.textgame.engine.narrator

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase

interface SentenceElement {
}

data class TextElement(val text: String) : SentenceElement

data class PunctuationElement(val punctuation: String) : SentenceElement

data class NameElement(val name: NounPhrase, val referent: NamedEntity) : SentenceElement

fun joinElementList(items: List<SentenceElement>, conjunction: String = "and"): List<SentenceElement> {
    if (items.size == 1)
        return mutableListOf(items[0])

    if (items.size == 2)
        return mutableListOf(items[0], TextElement(conjunction), items[1])

    val elements = mutableListOf<SentenceElement>()
    for (i in 0 until items.size-1) {
        elements.add(items[i])
        elements.add(PunctuationElement(","))
    }
    elements.add(TextElement(conjunction))
    elements.add(items[items.size-1])

    return elements
}

fun flattenElementList(items: List<List<SentenceElement>>, conjunction: String = "and"): List<SentenceElement> {
    if (items.size == 1)
        return items[0]

    if (items.size == 2) {
        val elements = mutableListOf<SentenceElement>()
        elements.addAll(items[0])
        elements.add(TextElement(conjunction))
        elements.addAll(items[1])
    }

    val elements = mutableListOf<SentenceElement>()

    for (i in 0 until items.size-1) {
        elements.addAll(items[i])
        elements.add(PunctuationElement(","))
    }
    elements.add(TextElement(conjunction))
    elements.addAll(items[items.size-1])

    return elements
}
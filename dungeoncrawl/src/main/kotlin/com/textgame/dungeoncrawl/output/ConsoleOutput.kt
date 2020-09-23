package com.textgame.dungeoncrawl.output

import com.textgame.engine.format.NounPhraseFormatter
import com.textgame.engine.narrator.NameElement
import com.textgame.engine.narrator.Paragraph
import com.textgame.engine.narrator.PunctuationElement
import com.textgame.engine.narrator.TextElement
import java.lang.IllegalArgumentException
import java.lang.System.out
import java.util.regex.Pattern

class ConsoleOutput(
        private val formatter: NounPhraseFormatter,
        private val consoleWidth: Int,
        private val indent: Int = 3
): GameOutput {

    private var lineBuilder = StringBuilder()
    private var width = indent

    private val COLOR_START_PATTERN = Pattern.compile("\u001B\\[..?(;1)?m")

    override fun println(string: String) {
        beginParagraph()
        write(string.split(" "))
        out.println(lineBuilder)
        println()
    }

    override fun printParagraph(paragraph: Paragraph) {
        val words = mutableListOf<String>()

        for (sentence in paragraph.sentences) {
            var capitalize = true
            for (sentenceElement in sentence) {
                when (sentenceElement) {
                    is TextElement -> words.add(sentenceElement.text)
                    is NameElement -> words.add(formatter.format(sentenceElement.name, sentenceElement.referent, capitalize))
                    is PunctuationElement -> {
                        val lastWord = words.removeAt(words.size - 1)
                        words.add(lastWord + sentenceElement.punctuation)
                    }
                    else -> throw IllegalArgumentException()
                }
                capitalize = false
            }
        }

        beginParagraph()
        write(words)
        out.println(lineBuilder)
        println()
    }

    private fun write(elements: List<String>) {
        for (element in elements) {
            for (word in element.split(" ")) {
                val wordLength = length(word)
                when {
                    // TODO no space before the first word in a line
                    width + wordLength + 1 <= consoleWidth -> {
                        // This word, plus a space, fits on the line
                        lineBuilder.append(" ").append(word)
                        width += wordLength + 1
                    }
                    else -> {
                        lineBuilder.appendln()
                        width = wordLength
                        lineBuilder.append(word)
                    }
                }
            }
        }
    }

    private fun beginParagraph() {
        lineBuilder = StringBuilder().append(" ".repeat(indent))
        width = indent
    }

    override fun println() {
        out.println()
    }

    fun length(string: String): Int {
        var length = string.length

        val matcher = COLOR_START_PATTERN.matcher(string)
        while(matcher.find()) {
            val match = matcher.toMatchResult()
            length -= (match.end() - match.start())
        }

        return length
    }

}
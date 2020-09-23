package com.textgame.dungeoncrawl.output

import com.textgame.engine.narrator.Paragraph

interface GameOutput {

    fun printParagraph(paragraph: Paragraph)

    fun println(string: String)

    fun println()
}
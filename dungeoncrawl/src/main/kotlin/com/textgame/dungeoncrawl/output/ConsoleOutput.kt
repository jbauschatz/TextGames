package com.textgame.dungeoncrawl.output

class ConsoleOutput(val maxWidth: Int, val indent: Int = 0): GameOutput {

    override fun println(sentence: String) {
        val words = sentence.split(" ")

        val lineBuilder = StringBuilder()
        var width = 0;

        for (word in words) {
            when {
                width == 0 -> {
                    lineBuilder.append(" ".repeat(indent)).append(word)
                    width += indent + word.length
                }
                width + word.length + 1 <= maxWidth -> {
                    lineBuilder.append(" ").append(word)
                    width += word.length
                }
                else -> {
                    width = word.length;
                    lineBuilder.appendln().append(word)
                }
            }
        }

        lineBuilder.appendln()
        kotlin.io.println(lineBuilder)
    }

    override fun println() {
        kotlin.io.println()
    }
}
package com.textgame.dungeoncrawl.output

class ConsoleOutput: GameOutput {

    override fun println(sentence: String) {
        System.out.println(sentence + System.lineSeparator())
    }

    override fun println() {
        System.out.println()
    }
}
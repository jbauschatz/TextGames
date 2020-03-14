package com.textgame.dungeoncrawl

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val playerInput = if (args.contains("--ai"))
                InputOption.AI_CONTROL
            else
                InputOption.PLAYER_INPUT

            Game().begin(playerInput)
        }
    }
}
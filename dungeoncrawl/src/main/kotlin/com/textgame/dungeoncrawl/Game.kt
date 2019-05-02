package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.MoveCommand
import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.narrator.NarrativeContext
import com.textgame.engine.narrator.Narrator
import java.util.*

class Game {

    private val scanner = Scanner(System.`in`)

    private val narrator = Narrator(NarrativeContext())

    private val player = Creature(ProperNoun("Player"), Pronouns.SECOND_PERSON_SINGULAR)

    /**
     * Begins a new game and starts the game-loop
     */
    fun begin() {
        // Configure the Narrator for second person player narration
        narrator.overridePronouns(player, Pronouns.SECOND_PERSON_SINGULAR)

        System.out.println("Welcome to the game." + System.lineSeparator())

        while (true) {
            val command = readCommand()
            execute(command)
        }
    }

    /**
     * Executes the given [GameCommand], interpreting all side-effects and narrating them to the user as appropriate.
     */
    private fun execute(command: GameCommand) {
        when (command) {
            is MoveCommand -> execute(command)
        }
    }

    /**
     * Executes a [MoveCommand]
     */
    private fun execute(move: MoveCommand) {
        narrate(SimpleSentence(move.mover, "go", move.direction))
    }

    /**
     * Displays the given [SimpleSentence] to the user, as formatted by the configured [Narrator]
     */
    private fun narrate(sentence: SimpleSentence) =
            narrate(narrator.writeSentence(sentence))

    /**
     * Displays the given string to the user following standard formatting.
     */
    private fun narrate(string: String) =
            System.out.println(string + System.lineSeparator())

    /**
     * Reads input from the user until a well-formed command was entered.
     */
    private fun readCommand(): GameCommand {
        while (true) {
            System.out.print("> ")
            val input = scanner.nextLine()

            if (!input.isEmpty()) {
                val words = input.split(" ")
                val command = when (words[0]) {
                    "go", "move" -> parseMove(input, words)
                    else -> {
                        narrate("Invalid command.")
                        null
                    }
                }

                command?.let{
                    return command
                }
            }
        }
    }

    /**
     * Parse a given command as a move action.
     *
     * If the move is not a valid direction, prints an error message and returns null.
     */
    private fun parseMove(line: String, words: List<String>): GameCommand? {
        if (words.size == 1) {
            narrate("You must input a direction.")
            return null
        }
        val direction = words[1]
        val allDirections = listOf(CardinalDirection.NORTH, CardinalDirection.SOUTH, CardinalDirection.EAST, CardinalDirection.WEST)
        for (cardinalDirection in allDirections) {
            val cardinalDirectionName = NounPhraseFormatter.format(cardinalDirection.getName())
            if (direction.toLowerCase() == cardinalDirectionName.toLowerCase())
                return MoveCommand(player, cardinalDirection)
        }

        narrate("You cannot go that way.")
        return null
    }
}
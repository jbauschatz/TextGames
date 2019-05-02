package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.MoveCommand
import com.textgame.dungeoncrawl.model.Creature
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.model.map.MapGenerator
import com.textgame.engine.FormattingUtil
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

    lateinit var currentLocation: Location

    /**
     * Begins a new game and starts the game-loop
     */
    fun begin() {
        currentLocation = MapGenerator.generateSmallMap()

        // Configure the Narrator for second person player narration
        narrator.overridePronouns(player, Pronouns.SECOND_PERSON_SINGULAR)

        System.out.println("Welcome to the game." + System.lineSeparator())
        describeLocation()

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
     * Executes a [MoveCommand], by moving the appropriate Creature into its destination
     */
    private fun execute(move: MoveCommand) {
        narrate(SimpleSentence(move.mover, "go", move.direction))
        currentLocation = currentLocation.doors[move.direction]!!
        describeLocation()
    }

    private fun describeLocation() {
        narrate(NounPhraseFormatter.format(currentLocation.name, titleCase = true))
        narrate(currentLocation.description)

        val formattedDoors = currentLocation.doors.keys.map { NounPhraseFormatter.format(it.name) }
        narrate("You can go " + FormattingUtil.formatList(formattedDoors) + ".")
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
        val allDirections = currentLocation.doors.keys
        for (cardinalDirection in allDirections) {
            val cardinalDirectionName = NounPhraseFormatter.format(cardinalDirection.name)
            if (direction.toLowerCase() == cardinalDirectionName.toLowerCase())
                return MoveCommand(player, cardinalDirection)
        }

        narrate("You cannot go that way.")
        return null
    }
}
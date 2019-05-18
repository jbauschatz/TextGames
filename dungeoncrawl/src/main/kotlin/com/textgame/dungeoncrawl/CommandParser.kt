package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.command.*
import com.textgame.dungeoncrawl.model.Creature
import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import java.util.*

/**
 * Parses command line input into valid [GameCommand] instances, relative to a given [Creature] designated as the Player
 */
class CommandParser(private val player: Creature) {

    private val scanner = Scanner(System.`in`)

    /**
     * Reads input from the user until a well-formed command was entered.
     */
    fun readCommand(): GameCommand {
        while (true) {
            System.out.print("> ")
            val input = scanner.nextLine()

            if (input.isNotEmpty()) {
                val words = input.split(" ")
                val command = when (words[0]) {
                    "go", "move" -> parseMove(words)
                    "items", "inventory" -> parseInventory()
                    "get", "take" -> parseTakeItem(words)
                    "look" -> LookCommand(player, player.location)
                    "wait" -> WaitCommand(player)
                    "equip" -> parseEquipItem(words)
                    "attack" -> parseAttack(words)
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
    private fun parseMove(words: List<String>): MoveCommand? {
        if (words.size == 1) {
            narrate("You must input a direction.")
            return null
        }
        val direction = words[1]
        val allDirections = player.location.doors.keys
        for (cardinalDirection in allDirections) {
            val cardinalDirectionName = NounPhraseFormatter.format(cardinalDirection.name)
            if (direction.toLowerCase() == cardinalDirectionName.toLowerCase())
                return MoveCommand(player, cardinalDirection)
        }

        narrate("You cannot go that way.")
        return null
    }

    private fun parseInventory() =
            InventoryCommand(player)

    private fun parseTakeItem(words: List<String>): TakeItemCommand? {
        if (words.size == 1) {
            narrate("Specify the name of an item to take.")
            return null
        }

        val name = parseDirectObject(words)
        val itemsByName = player.location.inventory.findByName(name)

        return when {
            itemsByName.isEmpty() -> {
                narrate("There is nothing here by that name.")
                null
            }
            itemsByName.size > 1 -> {
                narrate("There are multiple items by that name. Try being more specific.")
                null
            }
            else -> {
                val item = itemsByName[0]
                TakeItemCommand(player, item, player.location)
            }
        }
    }

    private fun parseEquipItem(words: List<String>): EquipItemCommand? {
        if (words.size == 1) {
            narrate("Specify the name of an item you carry to equip.")
        }

        val name = parseDirectObject(words)
        val itemsByName = player.inventory.findByName(name)

        if (itemsByName.isEmpty()) {
            narrate("You don't carry anything by that name.")
            return null
        }
        return if (itemsByName.size > 1) {
            narrate("You carry multiple items by that name. Try being more specific.")
            null
        } else {
            EquipItemCommand(player, itemsByName[0])
        }
    }

    private fun parseAttack(words: List<String>): AttackCommand? {
        val allEnemiesInLocation = player.location.creatures.members().filter { it != player }
        // TODO filter this list to enemies of the Player

        if (allEnemiesInLocation.isEmpty()) {
            narrate("There are no enemies here to attack.")
            return null
        }

        if (words.size == 1) {
            // TODO if there is only one enemy in the location, pick it
            narrate("Specify the name of an enemy to attack.")
            return null
        }

        val enemyName = parseDirectObject(words)
        val namedEnemies = player.location.creatures.findByName(enemyName)
        // TODO filter this list to enemies of the Player

        return when {
            namedEnemies.isEmpty() -> {
                narrate("There are no enemies here by that name.")
                null
            }
            namedEnemies.size > 1 -> {
                narrate("There are multiple enemies here by that name. Try being more specific.")
                null
            }
            else -> AttackCommand(player, namedEnemies[0], player.weapon)
        }
    }

    /**
     * Displays the given string to the user following standard formatting.
     */
    private fun narrate(string: String) =
            System.out.println(string + System.lineSeparator())

    private fun parseDirectObject(words: List<String>): String =
            words.subList(1, words.size).joinToString(" ")
}
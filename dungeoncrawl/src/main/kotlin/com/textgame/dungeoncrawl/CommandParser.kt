package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.command.*
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Weapon
import com.textgame.dungeoncrawl.output.CommandFormatter
import com.textgame.dungeoncrawl.strategy.CreatureStrategy
import com.textgame.engine.format.DefaultNounPhraseFormatter
import enemies
import java.lang.System.out
import java.util.*

/**
 * Parses command line input into valid [GameCommand] instances, relative to a given [Creature] designated as the Player
 */
object CommandParser: CreatureStrategy {

    private val scanner = Scanner(System.`in`)

    /**
     * Wraps a [CommandParser] so that it will produce output as if the user entered it into the [CommandParser].
     *
     * This is to facilitate AI play, or whenever a [Creature]'s commands should be output to the console as if they were
     * user input.
     */
    fun wrap(strategy: CreatureStrategy): CreatureStrategy {
        return object : CreatureStrategy {
            override fun act(creature: Creature): GameCommand? {
                val command = strategy.act(creature)

                if (command != null) {
                    out.println("> ${CommandFormatter.format(command)}")
                } else {
                    out.println("> ")
                }
                return command
            }
        }
    }

    /**
     * Reads input from the user until a well-formed command was entered.
     */
    override fun act(creature: Creature): GameCommand {
        while (true) {
            out.print("> ")
            val input = scanner.nextLine()

            if (input.isNotEmpty()) {
                val words = input.split(" ")
                val command = when (words[0]) {
                    "go", "move" -> parseMove(creature, words)
                    "items", "inventory" -> parseInventory(creature)
                    "get", "take" -> parseTakeItem(creature, words)
                    "look" -> LookCommand(creature, creature.location)
                    "wait" -> WaitCommand(creature)
                    "equip" -> parseEquipItem(creature, words)
                    "attack" -> parseAttack(creature, words)
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
    private fun parseMove(creature: Creature, words: List<String>): MoveCommand? {
        if (words.size == 1) {
            narrate("You must input a direction.")
            return null
        }
        val direction = words[1]

        for (door in creature.location.doors) {
            val cardinalDirectionName = DefaultNounPhraseFormatter.format(door.direction.name)
            if (direction.toLowerCase() == cardinalDirectionName.toLowerCase())
                return MoveCommand(creature, door.direction)
        }

        narrate("You cannot go that way.")
        return null
    }

    private fun parseInventory(creature: Creature) =
            InventoryCommand(creature)

    private fun parseTakeItem(creature: Creature, words: List<String>): TakeItemCommand? {
        if (words.size == 1) {
            narrate("Specify the name of an item to take.")
            return null
        }

        val name = parseDirectObject(words)
        val itemsByName = creature.location.findByName(name)

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
                TakeItemCommand(creature, item, creature.location)
            }
        }
    }

    private fun parseEquipItem(creature: Creature, words: List<String>): EquipWeaponCommand? {
        if (words.size == 1) {
            narrate("Specify the name of an item you carry to equip.")
        }

        val name = parseDirectObject(words)
        val itemsByName = creature.inventory.findByName(name)

        if (itemsByName.isEmpty()) {
            narrate("You don't carry anything by that name.")
            return null
        }
        if (itemsByName.size > 1) {
            narrate("You carry multiple items by that name. Try being more specific.")
            return null
        } else {
            val item = itemsByName[0]
            if (item !is Weapon) {
                narrate("You can only equip weapons.")
                return null
            }

            return EquipWeaponCommand(creature, item)
        }
    }

    private fun parseAttack(creature: Creature, words: List<String>): AttackCommand? {
        val allEnemiesInLocation = creature.enemies()

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
        val namedEnemies = creature.location.creatures.findByName(enemyName)
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
            else -> AttackCommand(creature, namedEnemies[0], creature.weapon)
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
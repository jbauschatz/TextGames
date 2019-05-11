package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.command.*
import com.textgame.dungeoncrawl.event.*
import com.textgame.dungeoncrawl.model.Creature
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.model.map.MapGenerator
import com.textgame.engine.model.nounphrase.*
import java.util.*

class Game {

    private val scanner = Scanner(System.`in`)

    private val player = Creature(ProperNoun("Player"), Pronouns.SECOND_PERSON_SINGULAR)

    private val listeners: MutableList<GameEventListener> = mutableListOf()

    lateinit var currentLocation: Location

    /**
     * Begins a new game and starts the game-loop
     */
    fun begin() {
        listeners.add(PlayerNarrator(player))

        currentLocation = MapGenerator.generateSmallMap()

        // Player's starting equipment
        player.inventory.add(Item(Adjective("small", Noun("key"))))
        player.inventory.add(Item(Adjective("rusty", Noun("dagger"))))

        System.out.println("Welcome to the game." + System.lineSeparator())
        execute(LookCommand())

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
            is InventoryCommand -> execute(command)
            is TakeItemCommand -> execute(command)
            is LookCommand -> execute(command)
            else -> throw IllegalArgumentException("Cannot execute command: " + command.javaClass)
        }
    }

    private fun dispatchEvent(event: GameEvent) =
            listeners.forEach { it.handleEvent(event) }

    /**
     * Executes a [MoveCommand], by moving the appropriate Creature into its destination
     */
    private fun execute(move: MoveCommand) {
        // TODO this assumes the player is moving
        val originalLocation = currentLocation
        val newLocation = originalLocation.doors[move.direction]!!

        // Move the Creature
        currentLocation = newLocation

        dispatchEvent(MoveEvent(player, move.direction, originalLocation, newLocation))
    }

    /**
     * Executes an [InventoryCommand], by listing the player's current inventory
     */
    private fun execute(inventory: InventoryCommand) {
        dispatchEvent(InventoryEvent(player))
    }

    /**
     * Executes a [TakeItemCommand] by transferring the [Item] from the [Location]'s [Inventory] to the acting [Creature]'s
     */
    private fun execute(takeItem: TakeItemCommand) {
        // Transfer the item from the Location to the Creature
        takeItem.location.inventory.remove(takeItem.item)
        takeItem.actor.inventory.add(takeItem.item)

        dispatchEvent(TakeItemEvent(takeItem.actor, takeItem.item, takeItem.location))
    }

    /**
     * Executes a [LookCommand] by describing the player's current [Location]
     */
    private fun execute(look: LookCommand) {
        dispatchEvent(LookEvent(player, currentLocation))
    }

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
                    "go", "move" -> parseMove(words)
                    "items", "inventory" -> parseInventory()
                    "get", "take" -> parseTakeItem(words)
                    "look" -> LookCommand()
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
        val allDirections = currentLocation.doors.keys
        for (cardinalDirection in allDirections) {
            val cardinalDirectionName = NounPhraseFormatter.format(cardinalDirection.name)
            if (direction.toLowerCase() == cardinalDirectionName.toLowerCase())
                return MoveCommand(player, cardinalDirection)
        }

        narrate("You cannot go that way.")
        return null
    }

    private fun parseInventory() =
            InventoryCommand()

    private fun parseTakeItem(words: List<String>): TakeItemCommand? {
        if (words.size == 1) {
            narrate("Specify the name of an item to take.")
            return null
        }

        val name = words.subList(1, words.size).joinToString(" ")
        val itemsByName = currentLocation.inventory.findByName(name)

        when {
            itemsByName.isEmpty() -> {
                narrate("There is nothing here by that name.")
                return null
            }
            itemsByName.size > 1 -> {
                narrate("There are multiple items by that name. Try being more specific.")
                return null
            }
            else -> {
                val item = itemsByName[0]
                return TakeItemCommand(player, item, currentLocation)
            }
        }
    }

}
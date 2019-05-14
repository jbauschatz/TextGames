package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.command.*
import com.textgame.dungeoncrawl.event.*
import com.textgame.dungeoncrawl.model.Creature
import com.textgame.dungeoncrawl.model.Inventory
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.model.map.MapGenerator
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.ProperNoun

class Game {

    private val listeners: MutableList<GameEventListener> = mutableListOf()

    /**
     * Begins a new game and starts the game-loop
     */
    fun begin() {
        // Initialize the Player with their starting location and equipment
        val startingLocation = MapGenerator.generateSmallMap()
        val player = Creature(ProperNoun("Player"), Pronouns.SECOND_PERSON_SINGULAR, startingLocation)
        player.inventory.add(Item(Adjective("small", Noun("key"))))
        player.inventory.add(Item(Adjective("rusty", Noun("dagger"))))

        // Configure input and output for the Player
        listeners.add(PlayerNarrator(player))
        val playerParser = CommandParser(player)

        // Opening game narration
        System.out.println("Welcome to the game." + System.lineSeparator())
        execute(LookCommand(player, startingLocation))

        // Begin the game loop
        while (true) {
            val command = playerParser.readCommand()
            execute(command)
        }
    }

    /**
     * Executes the given [GameCommand], interpreting all side-effects and dispatching events to listeners
     */
    private fun execute(command: GameCommand) {
        when (command) {
            is MoveCommand -> execute(command)
            is InventoryCommand -> execute(command)
            is TakeItemCommand -> execute(command)
            is LookCommand -> execute(command)
            is WaitCommand -> execute(command)
            else -> throw IllegalArgumentException("Cannot execute command: " + command.javaClass)
        }
    }

    /**
     * Dispatches the [GameEvent] to all listeners
     */
    private fun dispatchEvent(event: GameEvent) =
            listeners.forEach { it.handleEvent(event) }

    /**
     * Executes a [MoveCommand], by moving the appropriate Creature into its destination
     *
     * Dispatches a [MoveEvent] representing the change in state
     */
    private fun execute(move: MoveCommand) {
        val originalLocation = move.actor.location
        val newLocation = originalLocation.doors[move.direction]!!

        // Move the Creature
        move.actor.location = newLocation
        originalLocation.creatures.remove(move.actor)
        newLocation.creatures.add(move.actor)

        dispatchEvent(MoveEvent(move.actor, move.direction, originalLocation, newLocation))
    }

    private fun execute(look: LookCommand) =
            dispatchEvent(LookEvent(look.actor, look.location))

    /**
     * Executes an [InventoryCommand] by dispatching an [InventoryEvent] on the actor's behalf
     */
    private fun execute(inventory: InventoryCommand) =
            dispatchEvent(InventoryEvent(inventory.actor))

    /**
     * Executes a [TakeItemCommand] by transferring the [Item] from the [Location]'s [Inventory] to the acting [Creature]'s
     *
     * Dispatches a [TakeItemEvent] representing the change in state
     */
    private fun execute(takeItem: TakeItemCommand) {
        takeItem.location.inventory.remove(takeItem.item)
        takeItem.actor.inventory.add(takeItem.item)

        dispatchEvent(TakeItemEvent(takeItem.actor, takeItem.item, takeItem.location))
    }

    /**
     * Executes a [WaitCommand] by dispatching a [WaitEvent] for the acting [Creature]
     */
    private fun execute(wait: WaitCommand) =
            dispatchEvent(WaitEvent(wait.actor))
}
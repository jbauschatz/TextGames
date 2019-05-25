package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.command.*
import com.textgame.dungeoncrawl.event.*
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.Inventory
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.model.map.MapGenerator.Companion.generateSmallMap
import com.textgame.dungeoncrawl.output.ConsoleOutput
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.ItemView
import com.textgame.engine.model.NamedEntity.Companion.nextId
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.ProperNoun

class Game {

    private val creatureListeners: MutableMap<Creature, GameEventListener> = mutableMapOf()

    /**
     * All creatures which are currently alive in the game world
     */
    private val creatures: MutableList<Creature> = mutableListOf()

    /**
     * Begins a new game and starts the game-loop
     */
    fun begin() {
        val map = generateSmallMap()

        // Initialize the Player with their starting location and equipment
        val player = Creature(nextId(), ProperNoun("Player"), Pronouns.SECOND_PERSON_SINGULAR, map.playerStartingLocation, CommandParser)
        player.inventory.add(Item(nextId(), Adjective("small", Noun("key"))))
        player.inventory.add(Item(nextId(), Adjective("rusty", Noun("dagger"))))
        map.playerStartingLocation.creatures.add(player)

        // Assemble all Creatures existing on the Map
        map.locations.forEach {
            creatures.addAll(it.creatures.members())
        }

        // Configure narration for the Player
        creatureListeners[player] = PlayerNarrator(player, ConsoleOutput())

        // Opening game narration
        System.out.println("Welcome to the game." + System.lineSeparator())
        execute(LookCommand(player, player.location))

        // Begin the game loop
        while (true) {
            creatures.forEach {
                val command = it.strategy.act(it)
                execute(command)
            }
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
            is EquipItemCommand -> execute(command)
            is AttackCommand -> execute(command)
            else -> throw IllegalArgumentException("Cannot execute command: " + command.javaClass)
        }
    }

    /**
     * Dispatches the [GameEvent] to all listeners
     */
    private fun dispatchEvent(event: GameEvent, vararg locations: Location) {
        creatureListeners.keys.forEach {
            if (it.location in locations) {
                creatureListeners[it]!!.handleEvent(event)
            }
        }
    }

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

        dispatchEvent(
                MoveEvent(move.actor, move.direction, originalLocation, newLocation),
                originalLocation,
                newLocation
        )
    }

    private fun execute(look: LookCommand) =
            dispatchEvent(
                    LookEvent(look.actor, look.location),
                    look.location
            )

    /**
     * Executes an [InventoryCommand] by dispatching an [InventoryEvent] on the actor's behalf
     */
    private fun execute(inventory: InventoryCommand) =
            dispatchEvent(
                    InventoryEvent(inventory.actor),
                    inventory.actor.location
            )

    /**
     * Executes a [TakeItemCommand] by transferring the [Item] from the [Location]'s [Inventory] to the acting [Creature]'s
     *
     * Dispatches a [TakeItemEvent] representing the change in state
     */
    private fun execute(takeItem: TakeItemCommand) {
        takeItem.location.inventory.remove(takeItem.item)
        takeItem.actor.inventory.add(takeItem.item)

        dispatchEvent(
                TakeItemEvent(takeItem.actor, takeItem.item, takeItem.location),
                takeItem.location
        )
    }

    /**
     * Executes a [WaitCommand] by dispatching a [WaitEvent] for the acting [Creature]
     */
    private fun execute(wait: WaitCommand) =
            dispatchEvent(
                    WaitEvent(wait.actor),
                    wait.actor.location
            )

    /**
     * Executes an [EquipItemCommand] by transferring the [Item] from the [Creature]'s [Inventory] to its [Creature.weapon] slot
     *
     * Dispatches an [EquipItemEvent] representing the change in state
     */
    private fun execute(equip: EquipItemCommand) {
        // TODO if the Creature already has an item equipped, how should this be handled?

        equip.actor.inventory.remove(equip.item)
        equip.actor.weapon = equip.item

        dispatchEvent(
                EquipItemEvent(equip.actor, equip.item),
                equip.actor.location
        )
    }

    /**
     * Executes an [AttackCommand] by resolving the effects of the attack.
     *
     * Dispatches an [AttackEvent] within the [Location] of the attack.
     */
    private fun execute(attack: AttackCommand) {
        // TODO resolve the effects of combat
        val weaponView = if (attack.weapon != null) { ItemView(attack.weapon) } else { null }
        dispatchEvent(
                AttackEvent(CreatureView(attack.attacker), CreatureView(attack.defender), weaponView),
                attack.attacker.location
        )
    }
}
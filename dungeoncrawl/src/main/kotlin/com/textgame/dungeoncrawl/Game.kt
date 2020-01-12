package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.command.*
import com.textgame.dungeoncrawl.event.*
import com.textgame.dungeoncrawl.model.Inventory
import com.textgame.dungeoncrawl.model.creature.ActionType
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.model.map.MapGenerator.Companion.generateDungeon
import com.textgame.dungeoncrawl.output.ConsoleOutput
import com.textgame.dungeoncrawl.strategy.CompanionStrategy
import com.textgame.dungeoncrawl.strategy.IdleStrategy
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.ItemView
import com.textgame.dungeoncrawl.view.LocationView
import com.textgame.engine.model.NamedEntity.Companion.nextId
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.ProperNoun
import hasMoreActions
import isDead
import resetActions
import spendAction
import spendAllActions
import takeDamage

class Game {

    private val creatureListeners: MutableMap<Creature, GameEventListener> = mutableMapOf()

    /**
     * All creatures which are currently alive in the game world
     */
    private val creatures: MutableList<Creature> = mutableListOf()

    private val endConditions: MutableSet<GameCondition> = mutableSetOf()

    /**
     * Begins a new game and starts the game-loop
     */
    fun begin() {
        val map = generateDungeon()

        // Initialize the Player with their starting location and equipment
        val player = Creature(nextId(), ProperNoun("Player"), Pronouns.SECOND_PERSON_SINGULAR, 100, map.playerStartingLocation, IdleStrategy)
        player.allyGroups.add("PLAYER")
        player.addItem(Item(nextId(), Adjective("small", Noun("key"))))
        player.addItem(Item(nextId(), Adjective("rusty", Noun("dagger"))))

        val playerWeapon = Item(nextId(), Adjective("iron", Adjective("short", Noun("sword"))))
        player.addItem(playerWeapon)
        player.weapon = playerWeapon

        map.playerStartingLocation.creatures.add(player)

        // Establish an end condition for the Player's death
        endConditions.add(DeathCondition(player))

        // Initialize the Player's Companion
        val companionStrategy = CompanionStrategy(player)
        val companion = Creature(nextId(), ProperNoun("Lydia"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE, 100, map.playerStartingLocation, companionStrategy)
        companion.allyGroups.add("PLAYER")

        val companionWeapon = Item(nextId(), Noun("warhammer"))
        companion.addItem(companionWeapon)
        companion.weapon = companionWeapon

        map.playerStartingLocation.creatures.add(companion)

        // Assemble all Creatures existing on the Map (including Player and Companion)
        map.locations.forEach {
            creatures.addAll(it.creatures.members())
        }

        // Configure narration for the Player
        val playerInputOutput = PlayerController(player, ConsoleOutput())
        creatureListeners[player] = playerInputOutput

        // Announce the game's initialization
        dispatchEvent(GameStartEvent(LocationView(map.playerStartingLocation)), player.location)

        gameLoop()
    }

    /**
     * Begins the game's main loop, which will continue until an end condition is met
     */
    private fun gameLoop() {
        while (true) {
            creatures.forEach {
                // Restore the creature's actions for this turn
                it.resetActions()

                // Receive actions from the creature until it cannot act anymore
                while (it.hasMoreActions() && !it.isDead()) {
                    val command = it.strategy.act(it)
                    execute(command)

                    if (isGameOver()) {
                        dispatchEvent(GameOverEvent())
                        System.exit(0)
                    }
                }
            }

            // TODO remove dead Creatures from the list
        }
    }

    /**
     * Determines whether any end of game conditions are currently met
     */
    private fun isGameOver(): Boolean =
            endConditions.any { it.isTriggered() }

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
     * Dispatches the [GameEvent] to all listeners associated with [Creature]s within the specified [Location]s
     */
    private fun dispatchEvent(event: GameEvent, vararg locations: Location) {
        creatureListeners.keys.forEach {
            if (it.location in locations) {
                creatureListeners[it]!!.handleEvent(event)
            }
        }
    }

    /**
     * Dispatches the [GameEvent] to all listeners
     */
    private fun dispatchEvent(event: GameEvent) {
        creatureListeners.keys.forEach {
            creatureListeners[it]!!.handleEvent(event)
        }
    }

    /**
     * Executes a [MoveCommand], by moving the appropriate Creature into its destination
     *
     * Dispatches a [MoveEvent] representing the change in state
     */
    private fun execute(move: MoveCommand) {
        move.actor.spendAction(ActionType.MOVE)

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
        takeItem.actor.addItem(takeItem.item)

        dispatchEvent(
                TakeItemEvent(takeItem.actor, takeItem.item, takeItem.location),
                takeItem.location
        )
    }

    /**
     * Executes a [WaitCommand] by expending all the acting [Creature]'s actions
     *
     * Dispatches a [WaitEvent] for the acting [Creature]
     */
    private fun execute(wait: WaitCommand) {
        wait.actor.spendAllActions()

        dispatchEvent(
                WaitEvent(wait.actor),
                wait.actor.location
        )
    }

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
        var defender = attack.defender

        attack.attacker.spendAction(ActionType.ATTACK)

        // Resolve damage
        val damageDealt = 10
        defender.takeDamage(damageDealt)
        val lethal = defender.isDead()

        if (lethal) {
            defender.location.inventory.add(createCorpse(defender))
            defender.location.creatures.remove(defender)
        }

        // Dispatch Attack event
        val weaponView = if (attack.weapon != null) { ItemView(attack.weapon) } else { null }
        dispatchEvent(
                AttackEvent(CreatureView(attack.attacker), CreatureView(attack.defender), lethal, weaponView),
                attack.attacker.location
        )
    }

    private fun createCorpse(creature: Creature) =
            Item(nextId(), Adjective("dead", creature.name), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
}
package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.InputOption.*
import com.textgame.dungeoncrawl.command.*
import com.textgame.dungeoncrawl.event.*
import com.textgame.dungeoncrawl.model.Inventory
import com.textgame.dungeoncrawl.model.creature.ActionType
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Consumable
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.dungeoncrawl.model.map.MapGenerator.Companion.generateMap
import com.textgame.dungeoncrawl.model.map.MapGenerator.Companion.humanWeapon
import com.textgame.dungeoncrawl.model.map.MapGenerator.Companion.potionItem
import com.textgame.dungeoncrawl.output.ConsoleNounPhraseFormatter
import com.textgame.dungeoncrawl.output.ConsoleOutput
import com.textgame.dungeoncrawl.strategy.AdventurerStrategy
import com.textgame.dungeoncrawl.strategy.CreatureStrategy
import com.textgame.dungeoncrawl.strategy.IdleStrategy
import com.textgame.dungeoncrawl.strategy.companionStrategy
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.ItemView
import com.textgame.dungeoncrawl.view.LocationView
import com.textgame.dungeoncrawl.view.WeaponView
import com.textgame.engine.model.NamedEntity.Companion.nextId
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.Noun
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.ProperNoun
import hasMoreActions
import heal
import isDead
import resetActions
import spendAction
import spendAllActions
import takeDamage

enum class InputOption {
    PLAYER_INPUT,
    AI_CONTROL
}

class Game {

    private val creatureListeners: MutableMap<Creature, GameEventListener> = mutableMapOf()

    /**
     * All creatures which are currently alive in the game world
     */
    private val creatures: MutableList<Creature> = mutableListOf()

    private val endConditions: MutableSet<GameCondition> = mutableSetOf()

    /**
     * Begins a new game and starts the game-loop
     *
     * @param inputOption determines how the user will control the player character
     */
    fun begin(inputOption: InputOption) {
        val map = generateMap("map/dungeon.yaml")

        // Initialize the Player with their starting location and equipment
        val player = Creature(nextId(), ProperNoun("Player"), Pronouns.SECOND_PERSON_SINGULAR, 100, map.playerStartingLocation, IdleStrategy)
        player.allyGroups.add("PLAYER")
        player.addItem(Item(nextId(), Adjective("small", Noun("key"))))
        player.addItem(humanWeapon())

        val playerWeapon = humanWeapon()
        player.weapon = playerWeapon
        playerWeapon.addOwner(player)

        map.playerStartingLocation.creatures.add(player)

        // Initialize the Player's Companion
        val companionStrategy = companionStrategy(player)
        val companion = Creature(nextId(), ProperNoun("Lydia"), Pronouns.THIRD_PERSON_SINGULAR_FEMININE, 100, map.playerStartingLocation, companionStrategy)
        companion.takeDamage(75)
        companion.allyGroups.add("PLAYER")

        companion.weapon = humanWeapon()
        companion.weapon!!.addOwner(companion)

        companion.addItem(potionItem())
        companion.addItem(potionItem())

        map.playerStartingLocation.creatures.add(companion)

        // Assemble all Creatures existing on the GameMap (including Player and Companion)
        map.locations.forEach {
            creatures.addAll(it.creatures.members)
        }

        val enemies = creatures.filter { creature -> !creature.allyGroups.contains("PLAYER") }

        // Establish end conditions for the player dying, or killing all enemies
        endConditions.add(DeathCondition(listOf(player)))
        endConditions.add(DeathCondition(enemies))


        // Determine the source of input for the player
        val playerStrategy: CreatureStrategy = when(inputOption) {
            PLAYER_INPUT -> CommandParser
            AI_CONTROL -> CommandParser.wrap(AdventurerStrategy)
        }

        // Configure narration for the Player
        val playerInputOutput = PlayerController(player, playerStrategy, ConsoleOutput(ConsoleNounPhraseFormatter, 80))
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
                    if (command != null)
                        execute(command)
                    else
                        execute(WaitCommand(it))

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
            is EquipWeaponCommand -> execute(command)
            is UnequipWeaponCommand -> execute(command)
            is UseItemCommand -> execute(command)
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

        val door = originalLocation.doors.first { it.direction == move.direction }
        val newLocation = door.destination
        val inverseDoor = newLocation.doors.first { it.destination == originalLocation }

        // Move the Creature
        move.actor.location = newLocation
        originalLocation.creatures.remove(move.actor)
        newLocation.creatures.add(move.actor)

        dispatchEvent(
                MoveEvent(move.actor, move.direction, originalLocation, door, newLocation, inverseDoor),
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
        // The event should indicate the item before possession is changed
        val itemBeforeTake = ItemView(takeItem.item)

        takeItem.location.remove(takeItem.item)
        takeItem.actor.addItem(takeItem.item)

        dispatchEvent(
                TakeItemEvent(takeItem.actor, itemBeforeTake, takeItem.location),
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
     * Executes an [EquipWeaponCommand] by transferring the [Item] from the [Creature]'s [Creature.inventory] to its [Creature.weapon] slot
     *
     * Dispatches an [EquipWeaponEvent] representing the change in state
     */
    private fun execute(equip: EquipWeaponCommand) {
        // TODO if the Creature already has an item equipped, how should this be handled?

        equip.actor.inventory.remove(equip.weapon)
        equip.actor.weapon = equip.weapon

        dispatchEvent(
                EquipWeaponEvent(equip.actor, equip.weapon),
                equip.actor.location
        )
    }

    /**
     * Executes an [EquipWeaponCommand] by transferring the [Item] from the [Creature]'s [Creature.weapon] slot to its [Creature.inventory]
     *
     * Dispatches an [UnequipWeaponEvent] representing the change in state
     */
    private fun execute(unequip: UnequipWeaponCommand) {
        unequip.actor.weapon = null
        unequip.actor.inventory.add(unequip.weapon)

        dispatchEvent(
                UnequipWeaponEvent(unequip.actor, unequip.weapon),
                unequip.actor.location
        )
    }

    private fun execute(use: UseItemCommand) {
        if (use.item is Consumable) {
            use.actor.heal(use.item.healing)
            use.actor.inventory.remove(use.item)
            use.actor.spendAction(ActionType.ATTACK)

            dispatchEvent(
                    HealingItemEvent(use.actor, use.item),
                    use.actor.location
            )
        } else {
            throw UnsupportedOperationException("Unsupported item interaction")
        }
    }

    /**
     * Executes an [AttackCommand] by resolving the effects of the attack.
     *
     * Dispatches an [AttackEvent] within the [Location] of the attack.
     */
    private fun execute(attack: AttackCommand) {
        val defender = attack.defender

        attack.attacker.spendAction(ActionType.ATTACK)

        val hits: Boolean = isPercent(75)

        // Resolve damage
        val damageDealt = if (hits) 10 else 0

        if (hits) defender.takeDamage(damageDealt)
        val lethal = defender.isDead()

        if (lethal) {
            defender.location.inventory.add(createCorpse(defender))
            defender.location.creatures.remove(defender)
        }

        // Dispatch Attack event
        val weaponView = if (attack.weapon != null) { WeaponView(attack.weapon) } else { null }
        dispatchEvent(
                AttackEvent(CreatureView(attack.attacker), CreatureView(attack.defender), hits, lethal, weaponView),
                attack.attacker.location
        )
    }

    private fun createCorpse(creature: Creature) =
            Item(nextId(), Adjective("dead", creature.name), Pronouns.THIRD_PERSON_SINGULAR_NEUTER)
}
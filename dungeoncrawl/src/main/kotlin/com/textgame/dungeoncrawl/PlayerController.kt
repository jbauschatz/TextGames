package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.command.WaitCommand
import com.textgame.dungeoncrawl.event.*
import com.textgame.dungeoncrawl.model.creature.ActionType
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.CardinalDirection.Companion.opposite
import com.textgame.dungeoncrawl.model.sameEntity
import com.textgame.dungeoncrawl.output.ConsoleNounPhraseFormatter
import com.textgame.dungeoncrawl.output.GameOutput
import com.textgame.dungeoncrawl.strategy.CreatureStrategy
import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.ItemView
import com.textgame.dungeoncrawl.view.LocationView
import com.textgame.engine.FormattingUtil
import com.textgame.engine.model.GrammaticalPerson
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.NamedEntity.Companion.nextId
import com.textgame.engine.model.nounphrase.Definite
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_NEUTER
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.verb.Verb
import com.textgame.engine.narrator.NarrativeContext
import com.textgame.engine.narrator.Narrator
import com.textgame.engine.narrator.SentenceRealizer
import hasActionAvailable

private val SEE = Verb("sees", "see")

private val IS = Verb("is", "are")

/**
 * Entity to be used when narrating "there is" sentences.
 */
private val THERE = NamedEntity(nextId(), ProperNoun("there"), null)

/**
 * Class which allows a user to control a [Creature] designated as the Player, and to receive narration about events
 * in the game.
 *
 * This class mediates between a number of other classes to coordinate the input/output workflow.
 *
 * Game events are received and translated into [SimpleSentence] instances, which are then passed to a [Narrator] for
 * possible literary embellishment.
 *
 * Certain narration (like describing the Player's current location, or equipment) does not pass through the [Narrator],
 * because it is currently more convenient to just hard-code the output.
 *
 * When this class needs to provide a [GameCommand] on behalf of the Player, it first makes sure all previous narration
 * has been displayed to the console.
 */
class PlayerController(
        private val player: Creature,
        private val playerStrategy: CreatureStrategy,
        private val out: GameOutput
) : GameEventListener, CreatureStrategy {
    private val narrativeContext = NarrativeContext()
    private val formatter = ConsoleNounPhraseFormatter
    private val realizer = SentenceRealizer(narrativeContext)
    private val narrator = Narrator(realizer)

    init {
        player.strategy = this

        // Configure second person narration for the Player
        realizer.overridePerson(player, GrammaticalPerson.SECOND)

        // Player's starting items should be known objects in the Narrative Frame
        player.inventory.members.forEach {
            narrativeContext.addKnownEntity(it)
        }
    }

    /**
     * Gets a [GameCommand] for the Player via user input.
     *
     * This must first "flush" any sentences held in the [Narrator]'s cache, which it might still be trying to embellish.
     *
     * This delegates parsing the input to the [CommandParser].
     */
    override fun act(creature: Creature): GameCommand? {
        // Auto-pass the turn if the Player has already attacked (to improve the pace of gameplay)
        if (!creature.hasActionAvailable(ActionType.ATTACK))
            return WaitCommand(creature)

        flushNarration()

        return playerStrategy.act(creature)
    }

    /**
     * Receive a [GameEvent] that should be narrated to the player.
     *
     * Based on the type of event, this will typically translate the event into one or more [SimpleSentence]s that will
     * be processed by the [Narrator]. These will be later output once the [Narrator] has had enough time to embellish the
     * narrative, or we need to use the console for other purposes.
     */
    override fun handleEvent(event: GameEvent) {
        when (event) {
            is GameStartEvent -> handleGameStart(event)
            is GameOverEvent -> handleGameOver()
            is LookEvent -> handleLook(event)
            is InventoryEvent -> handleInventory()
            is MoveEvent -> handleMove(event)
            is TakeItemEvent -> handleTakeItem(event)
            is WaitEvent -> handleWait()
            is EquipItemEvent -> handleEquipItem(event)
            is UnequipItemEvent -> handleUnequipItem(event)
            is HealingItemEvent -> handleHealingItem(event)
            is AttackEvent -> handleAttack(event)
            else -> throw IllegalArgumentException("Invalid GameEvent type: ${event.javaClass}")
        }
    }

    private fun handleGameStart(gameStart: GameStartEvent) {
        narrate("Welcome to the game.")
        describeLocation(gameStart.startingLocation)

        val armed = player.weapon != null
        if (!armed) {
            narrate("You are unarmed.")
        } else {
            narrativeContext.addKnownEntity(player.weapon!!)
            narrate(String.format("You are armed with %s.",
                    formatter.format(player.weapon!!.name.indefinite(), player.weapon)))
        }
    }

    private fun handleGameOver() {
        narrate("Game Over.")
    }

    private fun handleLook(event: LookEvent) {
        describeLocation(event.location)
    }

    private fun handleInventory() {
        val armed = player.weapon != null
        val hasItems = player.inventory.members.isNotEmpty()

        if (hasItems) {
            if (armed) {
                narrate(String.format("You are armed with %s.",
                        formatter.format(player.weapon!!.name.indefinite(), player.weapon)))
            }

            val itemNames = player.inventory.members.map { formatter.format(it.name.indefinite(), it) }
            narrate("You carry " + FormattingUtil.formatList(itemNames) + ".")

            if (!armed) {
                narrate("You are unarmed.")
            }
        } else {
            if (armed) {
                narrate(String.format("You are armed with %s, and carry nothing else.",
                        formatter.format(player.weapon!!.name.indefinite(), player.weapon)))
            } else {
                narrate("You are unarmed, and carry nothing.")
            }
        }
    }

    private fun handleMove(event: MoveEvent) {
        if (sameEntity(player, event.actor)) {
            val go = Verb("goes", "go")
            narrate(SimpleSentence(player, VerbPredicate(go, event.direction)))
            describeLocation(event.toLocation)
        } else if (sameEntity(event.fromLocation, player.location)) {
            // An entity left the Player's location
            val exit = Verb("exits", "exit")
            narrate(SimpleSentence(event.actor, VerbPredicate(exit, event.direction)))
        } else if (sameEntity(event.toLocation, player.location)) {
            // An entity entered the Player's location
            val enter = Verb("enters", "enter")
            val properCardinalDirection = NamedEntity(
                    nextId(),
                    Definite(opposite(event.direction).name, true),
                    THIRD_PERSON_SINGULAR_NEUTER
            )

            val prep = pick(
                    PrepositionalPhrase("from", properCardinalDirection),
                    PrepositionalPhrase("through", event.toDoor)
            )
            narrate(SimpleSentence(event.actor, VerbPredicate(enter, prepositionalPhrase = prep)))
        }
    }

    private fun handleTakeItem(event: TakeItemEvent) {
        val take = Verb("takes", "take")

        /* Since the item has already changed possession, make a copy of it with no owner
           to make the narration more clear */
        val item = ItemView(
                event.item.id,
                event.item.name,
                event.item.pronouns
        )

        narrate(SimpleSentence(event.actor, VerbPredicate(take, item)))
    }

    private fun handleWait() {
    }

    private fun handleEquipItem(event: EquipItemEvent) {
        val equip = Verb("draws", "draw")
        narrate(SimpleSentence(event.actor, VerbPredicate(equip, event.item)))
    }

    private fun handleUnequipItem(event: UnequipItemEvent) {
        val unequip = Verb("sheathes", "sheathe")
        narrate(SimpleSentence(event.actor, VerbPredicate(unequip, event.item)))
    }

    private fun handleHealingItem(event: HealingItemEvent) {
        narrate(SimpleSentence(event.actor, VerbPredicate(event.verb, event.consumable)))
    }

    private fun handleAttack(event: AttackEvent) {
        val verb = Verb("attacks", "attack")

        if (event.weapon != null) {
            // Armed attack
            narrate(SimpleSentence(event.attacker, VerbPredicate(verb, event.defender, PrepositionalPhrase("with", event.weapon))))
        } else {
            // Unarmed attack
            narrate(SimpleSentence(event.attacker, VerbPredicate(verb, event.defender)))
        }

        if (event.isLethal)
            narrate(SimpleSentence(event.attacker, VerbPredicate(Verb("kills", "kill"), event.defender)))
    }

    private fun describeLocation(location: LocationView) {
        narrate(formatter.format(location.name, location, titleCase = true, capitalize = true))
        narrate(location.description)

        // List other creatures occupying the room
        val otherCreatures = location.creatures.filter { !sameEntity(it, player) }
        if (otherCreatures.isNotEmpty()) {
            otherCreatures.forEach {
                describeCreature(it)
            }
            flushNarration()
        }

        // List containers in the location
        if (location.containers.isNotEmpty())
            location.containers.forEach { container ->
                narrativeContext.removeKnownEntity(container)
                narrate(SimpleSentence(player, VerbPredicate(SEE, container)))

                container.slots.keys.forEach { slot ->
                    container.slots.getValue(slot).forEach { item ->
                        narrativeContext.removeKnownEntity(item)
                        narrate(SimpleSentence(
                                THERE,
                                VerbPredicate(IS, item, PrepositionalPhrase(slot, container))
                        ))
                    }
                }
            }

        // List loose items in the location
        if (location.items.isNotEmpty()) {
            location.items.forEach {
                narrativeContext.removeKnownEntity(it)
                narrate(SimpleSentence(player, VerbPredicate(SEE, it)))
            }
        }

        flushNarration()

        // List exits
        val leads = Verb("leads", "lead")
        location.doors.forEach {
            narrativeContext.removeKnownEntity(it)
            narrate(SimpleSentence(it, VerbPredicate(leads, it.direction)))
        }
    }

    private fun describeCreature(creature: CreatureView) {
        narrativeContext.removeKnownEntity(creature)
        if (creature.weapon != null)
            narrativeContext.removeKnownEntity(creature.weapon)

        narrate(SimpleSentence(player, VerbPredicate(SEE, creature)))
        if (creature.weapon != null)
            narrate(SimpleSentence(creature, VerbPredicate(Verb("wields", "wield"), creature.weapon)))
    }

    /**
     * Passes the [sentence] to the [narrator] for literary embellishment. It will be displayed later when narration is
     * flushed.
     */
    private fun narrate(sentence: SimpleSentence) =
            narrator.narrate(sentence)

    /**
     * Displays the given string to the user following standard formatting.
     */
    private fun narrate(string: String) {
        flushNarration()
        out.println(string)
    }

    /**
     * Forces the [narrator] to flush any narration which it has currently cached.
     *
     * This is necessary when we need to use the console to read input, or output something specific. At this point,
     * any embellishment the [narrator] has produced is considered "good enough" and all narration is output.
     */
    private fun flushNarration() {
        val paragraphs = narrator.flushParagraphs()
        paragraphs.forEach {
            out.printParagraph(it)
        }

        // Start fresh with pronouns due to the interruption in flow
        realizer.clearPronounHistory()
    }

}
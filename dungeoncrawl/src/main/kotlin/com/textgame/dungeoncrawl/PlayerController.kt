package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.command.GameCommand
import com.textgame.dungeoncrawl.event.*
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.sameEntity
import com.textgame.dungeoncrawl.output.GameOutput
import com.textgame.dungeoncrawl.strategy.CreatureStrategy
import com.textgame.dungeoncrawl.view.LocationView
import com.textgame.engine.FormattingUtil
import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.narrator.NarrativeContext
import com.textgame.engine.narrator.Narrator
import com.textgame.engine.narrator.SentenceRealizer

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
        private val out: GameOutput
): GameEventListener, CreatureStrategy {
    private val parser = CommandParser
    private val narrativeContext = NarrativeContext()
    private val realizer = SentenceRealizer(narrativeContext)
    private val narrator = Narrator(realizer)

    init {
        player.strategy = this

        // Configure second person narration for the Player
        realizer.overridePronouns(player, Pronouns.SECOND_PERSON_SINGULAR)

        // Player's starting items should be known objects in the Narrative Frame
        player.inventory.members().forEach {
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
    override fun act(creature: Creature): GameCommand {
        flushNarration()

        return parser.act(creature)
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
            is LookEvent -> handleLook(event)
            is InventoryEvent -> handleInventory()
            is MoveEvent -> handleMove(event)
            is TakeItemEvent -> handleTakeItem(event)
            is WaitEvent -> handleWait(event)
            is EquipItemEvent -> handleEquipItem(event)
            is AttackEvent -> handleAttack(event)
            else -> throw IllegalArgumentException("Invalid GameEvent type: ${event.javaClass}")
        }
    }

    private fun handleLook(event: LookEvent) {
        describeLocation(event.location)
    }

    private fun handleInventory() {
        val armed = player.weapon != null
        val hasItems = player.inventory.members().isNotEmpty()

        if (hasItems) {
            if (armed) {
                narrate(String.format("You are armed with %s.",
                        NounPhraseFormatter.format(player.weapon!!.name.indefinite())))
            }

            val itemNames = player.inventory.members().map { NounPhraseFormatter.format(it.name.indefinite()) }
            narrate("You carry " + FormattingUtil.formatList(itemNames) + ".")

            if (!armed) {
                narrate("You are unarmed.")
            }
        } else {
            if (armed) {
                narrate(String.format("You are armed with %s, and carry nothing else.",
                        NounPhraseFormatter.format(player.weapon!!.name.indefinite())))
            } else {
                narrate("You are unarmed, and carry nothing.")
            }
        }
    }

    private fun handleMove(event: MoveEvent) {
        if (sameEntity(player, event.actor)) {
            narrate(SimpleSentence(player, "go", event.direction))
            describeLocation(event.toLocation)
        }
    }

    private fun handleTakeItem(event: TakeItemEvent) {
        val verb = if (sameEntity(player, event.actor)) "take" else "takes"
        narrate(SimpleSentence(event.actor, verb, event.item))
    }

    private fun handleWait(event: WaitEvent) {
        val verb = if (sameEntity(player, event.actor)) "wait" else "waits"
        narrate(SimpleSentence(event.actor, verb))
    }

    private fun handleEquipItem(event: EquipItemEvent) {
        val verb = if (sameEntity(player, event.actor)) "equip" else "equips"
        narrate(SimpleSentence(event.actor, verb, event.item))
    }

    private fun handleAttack(event: AttackEvent) {
        val verb = if (sameEntity(player, event.attacker)) "attack" else "attacks"

        if (event.weapon != null) {
            // Armed attack
            narrate(SimpleSentence(event.attacker, verb, event.defender, PrepositionalPhrase("with", event.weapon)))
        } else {
            // Unarmed attack
            narrate(SimpleSentence(event.attacker, verb, event.defender))
        }
    }

    private fun describeLocation(location: LocationView) {
        narrate(NounPhraseFormatter.format(location.name, titleCase = true))
        narrate(location.description)

        // List other creatures occupying the room
        val otherCreatures = location.creatures.filter { !sameEntity(it, player) }
        if (otherCreatures.isNotEmpty()) {
            val otherCreatureNames = otherCreatures.map { NounPhraseFormatter.format(it.name.indefinite()) }
            narrate("You see " + FormattingUtil.formatList(otherCreatureNames) + ".")

            // Any named Creatures should be known in the Narrative Context
            otherCreatures.forEach { narrativeContext.addKnownEntity(it) }
        }

        // List items in the location
        if (location.items.isEmpty()) {
            narrate("You don't see anything of value here.")
        } else {
            val itemNames = location.items.map { NounPhraseFormatter.format(it.name.indefinite()) }
            narrate("You see " + FormattingUtil.formatList(itemNames) + ".")

            // Any named Items should be known in the Narrative Context
            location.items.forEach { narrativeContext.addKnownEntity(it) }
        }

        // List exits
        val formattedDoors = location.doors.map { NounPhraseFormatter.format(it.name) }
        narrate("You can go " + FormattingUtil.formatList(formattedDoors) + ".")
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
            out.println(it.sentences.joinToString(" "))
        }
    }

}
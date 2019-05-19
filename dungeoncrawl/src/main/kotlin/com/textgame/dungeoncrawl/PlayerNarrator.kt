package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.event.*
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.engine.FormattingUtil
import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.narrator.NarrativeContext
import com.textgame.engine.narrator.Narrator
import java.lang.IllegalArgumentException

/**
 * Narrates any [GameEvent] which the Player directly performed, or can observe
 * (typically because the event occurred within their current [Location])
 */
class PlayerNarrator(private val player: Creature): GameEventListener {

    private val narrator = Narrator(NarrativeContext())

    init {
        // Configure second person narration for the Player
        narrator.overridePronouns(player, Pronouns.SECOND_PERSON_SINGULAR)

        // Player's starting items should be known objects in the Narrative Frame
        player.inventory.members().forEach {
            narrator.narrativeContext.addKnownEntity(it)
        }
    }

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
        if (event.actor == player) {
            narrate(SimpleSentence(player, "go", event.direction))
            describeLocation(event.toLocation)
        }
    }

    private fun handleTakeItem(event: TakeItemEvent) {
        val verb = if (event.actor == player) "take" else "takes"
        narrate(SimpleSentence(event.actor, verb, event.item))
    }

    private fun handleWait(event: WaitEvent) {
        val verb = if (event.actor == player) "wait" else "waits"
        narrate(SimpleSentence(event.actor, verb))
    }

    private fun handleEquipItem(event: EquipItemEvent) {
        val verb = if (event.actor == player) "equip" else "equips"
        narrate(SimpleSentence(event.actor, verb, event.item))
    }

    private fun handleAttack(event: AttackEvent) {
        val verb = if (event.attacker == player) "attack" else "attacks"

        if (event.weapon != null) {
            // Armed attack
            narrate(SimpleSentence(event.attacker, verb, event.defender, PrepositionalPhrase("with", event.weapon)))
        } else {
            // Unarmed attack
            narrate(SimpleSentence(event.attacker, verb, event.defender))
        }
    }

    private fun describeLocation(location: Location) {
        narrate(NounPhraseFormatter.format(location.name, titleCase = true))
        narrate(location.description)

        // List other creatures occupying the room
        val otherCreatures = location.creatures.members().filter { it != player }
        if (otherCreatures.isNotEmpty()) {
            val otherCreatureNames = otherCreatures.map { NounPhraseFormatter.format(it.name.indefinite()) }
            narrate("You see " + FormattingUtil.formatList(otherCreatureNames) + ".")

            // Any named Creatures should be known in the Narrative Context
            otherCreatures.forEach { narrator.narrativeContext.addKnownEntity(it) }
        }

        // List items in the location
        if (location.inventory.members().isEmpty()) {
            narrate("You don't see anything of value here.")
        } else {
            val itemNames = location.inventory.members().map { NounPhraseFormatter.format(it.name.indefinite()) }
            narrate("You see " + FormattingUtil.formatList(itemNames) + ".")

            // Any named Items should be known in the Narrative Context
            location.inventory.members().forEach { narrator.narrativeContext.addKnownEntity(it) }
        }

        // List exits
        val formattedDoors = location.doors.keys.map { NounPhraseFormatter.format(it.name) }
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

}
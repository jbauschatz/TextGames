package com.textgame.dungeoncrawl

import com.textgame.dungeoncrawl.event.*
import com.textgame.dungeoncrawl.model.Creature
import com.textgame.dungeoncrawl.model.map.Location
import com.textgame.engine.FormattingUtil
import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import com.textgame.engine.model.nounphrase.Pronouns
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
            is LookEvent -> handle(event)
            is InventoryEvent -> handle(event)
            is MoveEvent -> handle(event)
            is TakeItemEvent -> handle(event)
            is WaitEvent -> handle(event)
            is EquipItemEvent -> handle(event)
            else -> throw IllegalArgumentException("Invalid GameEvent type: ${event.javaClass}")
        }
    }

    fun handle(event: LookEvent) {
        describeLocation(event.location)
    }

    fun handle(event: InventoryEvent) {
        if (player.inventory.members().isEmpty()) {
            narrate("You carry nothing.")
        } else {
            val itemNames = player.inventory.members().map { NounPhraseFormatter.format(it.name.indefinite()) }
            narrate("You carry " + FormattingUtil.formatList(itemNames) + ".")
        }
    }

    fun handle(event: MoveEvent) {
        if (event.actor == player) {
            narrate(SimpleSentence(player, "go", event.direction))
            describeLocation(event.toLocation)
        }
    }

    fun handle(event: TakeItemEvent) {
        val verb = if (event.actor == player) "take" else "takes"
        narrate(SimpleSentence(event.actor, verb, event.item))
    }

    fun handle(event: WaitEvent) {
        val verb = if (event.actor == player) "wait" else "waits"
        narrate(SimpleSentence(event.actor, verb))
    }

    fun handle(event: EquipItemEvent) {
        val verb = if (event.actor == player) "equip" else "equips"
        narrate(SimpleSentence(event.actor, verb, event.item))
    }

    private fun describeLocation(location: Location) {
        narrate(NounPhraseFormatter.format(location.name, titleCase = true))
        narrate(location.description)

        val otherCreatures = location.creatures.members().filter { it != player }
        if (!otherCreatures.isEmpty()) {
            val otherCreatureNames = otherCreatures.map { NounPhraseFormatter.format(it.name.indefinite()) }
            narrate("You see " + FormattingUtil.formatList(otherCreatureNames) + ".")
        }

        if (location.inventory.members().isEmpty()) {
            narrate("You don't see anything of value here.")
        } else {
            val itemNames = location.inventory.members().map { NounPhraseFormatter.format(it.name.indefinite()) }
            narrate("You see " + FormattingUtil.formatList(itemNames) + ".")
        }

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
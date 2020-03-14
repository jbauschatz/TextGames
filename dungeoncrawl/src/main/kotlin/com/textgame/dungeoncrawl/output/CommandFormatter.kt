package com.textgame.dungeoncrawl.output

import com.textgame.dungeoncrawl.CommandParser
import com.textgame.dungeoncrawl.command.*
import com.textgame.engine.model.nounphrase.NounPhraseFormatter

class CommandFormatter {

    companion object {

        /**
         * Formats a user-friendly string for a [GameCommand]
         *
         * This string must be consistent with [CommandParser], so passing the same command between the formatter and the
         * parser should have stable results.
         */
        fun format(command: GameCommand): String =
                when (command) {
                    is AttackCommand -> "attack ${NounPhraseFormatter.format(command.defender.name)}"
                    is UnequipItemCommand -> "unequip ${NounPhraseFormatter.format(command.item.name)}"
                    is UseItemCommand -> "use ${NounPhraseFormatter.format(command.item.name)}"
                    is MoveCommand -> "go ${NounPhraseFormatter.format(command.direction.name)}"
                    is LookCommand -> "look"
                    is EquipItemCommand -> "equip ${NounPhraseFormatter.format(command.item.name)}"
                    is TakeItemCommand -> "equip ${NounPhraseFormatter.format(command.item.name)}"
                    is WaitCommand -> "wait"
                    else -> "help"
                }
    }
}
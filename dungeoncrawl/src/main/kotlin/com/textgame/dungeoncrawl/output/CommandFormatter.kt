package com.textgame.dungeoncrawl.output

import com.textgame.dungeoncrawl.CommandParser
import com.textgame.dungeoncrawl.command.*
import com.textgame.engine.format.DefaultNounPhraseFormatter

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
                    is AttackCommand -> "attack ${DefaultNounPhraseFormatter.format(command.defender.name)}"
                    is UnequipWeaponCommand -> "unequip ${DefaultNounPhraseFormatter.format(command.weapon.name)}"
                    is UseItemCommand -> "use ${DefaultNounPhraseFormatter.format(command.item.name)}"
                    is MoveCommand -> "go ${DefaultNounPhraseFormatter.format(command.direction.name)}"
                    is LookCommand -> "look"
                    is EquipWeaponCommand -> "equip ${DefaultNounPhraseFormatter.format(command.weapon.name)}"
                    is TakeItemCommand -> "take ${DefaultNounPhraseFormatter.format(command.item.name)}"
                    is WaitCommand -> "wait"
                    else -> "help"
                }
    }
}
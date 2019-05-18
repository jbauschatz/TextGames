package com.textgame.engine.model

import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

/**
 * Base class for any distinct game element that can be identified by name
 */
abstract class NamedEntity(

        /**
         * Id which uniquely identifies this entity within the game world
         */
        val id: Int,

        val name: NounPhrase,

        val pronouns: Pronouns
) {

    companion object {
        private var id = 1

        fun nextId() =
                id++
    }

    override fun equals(other: Any?): Boolean =
        if (other is NamedEntity) {
            id == other.id
        } else {
            false
        }

    override fun hashCode(): Int =
            id
}
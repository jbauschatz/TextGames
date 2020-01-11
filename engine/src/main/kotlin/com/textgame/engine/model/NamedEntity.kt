package com.textgame.engine.model

import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

/**
 * Base class for any distinct entity that can be identified by name
 */
open class NamedEntity(

        /**
         * Id which uniquely identifies this entity
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
package com.textgame.engine.model.nounphrase

import com.textgame.engine.model.NamedEntity

class PartialNameMatcher {

    companion object {

        fun <EntityType : NamedEntity> findByName(name: String, objects: Collection<EntityType>): List<EntityType> =
                objects.filter { matches(name, it.name) }

        fun matches(name: String, nounPhrase: NounPhrase): Boolean =
                name.toLowerCase() == NounPhraseFormatter.format(nounPhrase).toLowerCase()
                        || when (nounPhrase) {
                            is Adjective -> matchesPartially(name, nounPhrase)
                            else -> false
                        }

        private fun matchesPartially(name: String, adjective: Adjective): Boolean =
                matches(name, adjective.stem)
    }
}
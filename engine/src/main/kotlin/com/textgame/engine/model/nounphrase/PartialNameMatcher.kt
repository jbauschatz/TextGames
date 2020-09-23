package com.textgame.engine.model.nounphrase

import com.textgame.engine.format.DefaultNounPhraseFormatter

class PartialNameMatcher {

    companion object {

        fun matches(name: String, nounPhrase: NounPhrase): Boolean =
                name.toLowerCase() == DefaultNounPhraseFormatter.format(nounPhrase).toLowerCase()
                        || when (nounPhrase) {
                            is Adjective -> matchesPartially(name, nounPhrase)
                            else -> false
                        }

        private fun matchesPartially(name: String, adjective: Adjective): Boolean =
                matches(name, adjective.stem)
    }
}
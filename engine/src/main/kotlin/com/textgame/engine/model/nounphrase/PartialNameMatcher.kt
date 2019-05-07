package com.textgame.engine.model.nounphrase

class PartialNameMatcher {

    companion object {

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
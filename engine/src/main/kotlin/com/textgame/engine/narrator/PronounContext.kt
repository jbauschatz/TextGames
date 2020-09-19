package com.textgame.engine.narrator

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.NounFunction
import com.textgame.engine.model.NounFunction.*
import com.textgame.engine.model.nounphrase.Pronouns

class PronounContext {

    private val pronounUsages: MutableMap<Pronouns, PronounUsage> = mutableMapOf();

    private data class PronounUsage(
            val entity: NamedEntity,
            val nounFunction: NounFunction
    )

    fun clear() {
        pronounUsages.clear()
    }

    fun recordPronounUsage(entity: NamedEntity, pronouns: Pronouns?, nounFunction: NounFunction) {
        if (pronouns == null)
            return

        // No existing usage, so record it
        if (pronounUsages[pronouns] == null) {
            pronounUsages[pronouns] = PronounUsage(entity, nounFunction);
            return
        }

        // There is an existing usage, so only overwrite it if we have priority
        val usage = pronounUsages[pronouns]!!
        if (priority(nounFunction) < priority(usage.nounFunction))
            pronounUsages[pronouns] = PronounUsage(entity, nounFunction)
    }

    fun shouldUsePronouns(entity: NamedEntity, pronouns: Pronouns?, nounFunction: NounFunction): Boolean {
        if (pronouns == null)
            return false

        if (pronounUsages[pronouns] == null)
            return false

        val usage = pronounUsages[pronouns]!!

        if (usage.entity == entity)
            return true

        return false
    }

    private fun priority(nounFunction: NounFunction): Int =
            when(nounFunction) {
                SUBJECT -> 0
                DIRECT_OBJECT -> 1
                OBJECT_OF_PREPOSITION -> 2
            }
}
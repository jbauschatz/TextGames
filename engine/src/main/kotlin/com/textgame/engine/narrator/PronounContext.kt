package com.textgame.engine.narrator

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.NounFunction
import com.textgame.engine.model.NounFunction.*
import com.textgame.engine.model.GrammaticalPerson
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.Pronouns.Companion.FIRST_PERSON_SINGULAR
import com.textgame.engine.model.nounphrase.Pronouns.Companion.SECOND_PERSON_SINGULAR
import java.lang.IllegalArgumentException

class PronounContext {

    private val pronounOverrides: MutableMap<NamedEntity, Pronouns> = mutableMapOf()

    /**
     * Records the most recent entity to be referred to by each unique set of [Pronouns]
     */
    private val pronounUsages: MutableMap<Pronouns, PronounUsage> = mutableMapOf()

    private data class PronounUsage(
            val entity: NamedEntity,
            val nounFunction: NounFunction
    )

    fun getPronouns(entity: NamedEntity) =
            if (pronounOverrides.containsKey(entity)) pronounOverrides[entity] else entity.pronouns

    fun overridePerson(namedEntity: NamedEntity, person: GrammaticalPerson) {
        pronounOverrides[namedEntity] = when (person) {
            GrammaticalPerson.SECOND -> SECOND_PERSON_SINGULAR
            GrammaticalPerson.FIRST -> FIRST_PERSON_SINGULAR
            else -> throw IllegalArgumentException("Unsupported GrammaticalPerson: $person")
        }
    }

    fun clearHistory() {
        pronounUsages.clear()
    }

    fun recordPronounUsage(entity: NamedEntity, nounFunction: NounFunction) {
        val pronounsToUse = getPronouns(entity)

        // If no pronouns are available to use, the context is unaffected
        if (pronounsToUse == null)
            return

        // This is the first usage, so record it
        if (pronounUsages[pronounsToUse] == null) {
            pronounUsages[pronounsToUse] = PronounUsage(entity, nounFunction);
            return
        }

        // There is an existing usage, so only overwrite it if we have priority
        val usage = pronounUsages[pronounsToUse]!!
        if (priority(nounFunction) < priority(usage.nounFunction))
            pronounUsages[pronounsToUse] = PronounUsage(entity, nounFunction)
    }

    fun shouldUsePronouns(entity: NamedEntity): Boolean {
        // If a set of pronouns have been enforced for the entity, assume they should always be used
        if (pronounOverrides.containsKey(entity))
            return true

        val pronouns = entity.pronouns

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
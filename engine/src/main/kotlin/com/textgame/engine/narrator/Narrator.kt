package com.textgame.engine.narrator

import com.textgame.engine.model.Case
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import com.textgame.engine.model.sentence.SimpleSentence
import java.lang.StringBuilder

class Narrator(
        private val narrativeContext: NarrativeContext
) {

    private val pronounOverride: MutableMap<NamedEntity, Pronouns> = HashMap()

    fun writeSentence(sentence: SimpleSentence): String {
        val subjectName = referToEntity(sentence.subject, sentence.subject, Case.NOMINATIVE)

        // Start with basic Subject/Verb
        val builder = StringBuilder()
                .append(NounPhraseFormatter.format(subjectName, true))
                .append(" ")
                .append(sentence.verb)

        // Include the Direct Object (if present)
        sentence.directObject?.let {
            val directObjectName = referToEntity(sentence.directObject, sentence.subject, Case.ACCUSATIVE)

            builder.append(" ")
                    .append(NounPhraseFormatter.format(directObjectName, false))
        }

        // Apply punctuation at the end of the sentence
        builder.append(".")

        return builder.toString()
    }

    /**
     * Configure the pronouns to be used when referring to the given entity.
     *
     * These pronouns will ALWAYS be used to refer to the entity, rather than its name or stated pronouns.
     * Use this typically to designate one entity as "you" or "I" throughout a narrative.
     */
    fun overridePronouns(entity: NamedEntity, pronouns: Pronouns) {
        pronounOverride[entity] = pronouns
    }

    /**
     * Determine how to refer to a [NamedEntity] in the context of the narrative.
     *
     * This is functionality is a major determiner of the character of the narration.
     *
     * This takes into account:
     * - Whether the entity has a fixed pronoun (ie "You" or "I") within the narration
     * - Whether the entity is being newly introduced into the context (and is indefinite) or is already known in
     * context (and is definite)
     * - Grammatical context (how is this word functioning within the sentence)
     *
     * As a side-effect, the [NamedEntity] will be considered a known entity in the [NarrativeContext], which will change
     * how it is referred to in the future.
     */
    protected fun referToEntity(namedEntity: NamedEntity, subjectOfSentence: NamedEntity, case: Case): NounPhrase {
        val name: NounPhrase;

        if (case == Case.ACCUSATIVE && namedEntity == subjectOfSentence) {
            // If the Subject and Direct Object are the same, refer to the Direct Object via reflexive pronoun
            // This may be the entity's native pronoun, or one configured via [overridePronouns(NamedEntity, Pronouns)]
            if (pronounOverride.containsKey(namedEntity))
                name = pronounOverride[namedEntity]!!.reflexive
            else
                name = namedEntity.pronouns.reflexive
        } else {
            name = when {
                pronounOverride.containsKey(namedEntity) -> pronounOverride[namedEntity]!!.get(case)
                narrativeContext.isKnownEntity(namedEntity) -> namedEntity.name.definite()
                else -> namedEntity.name.indefinite()
            }
        }

        narrativeContext.addKnownEntity(namedEntity)
        return name
    }
}
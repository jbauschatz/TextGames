package com.textgame.engine.narrator

import com.textgame.engine.FormattingUtil.Companion.formatList
import com.textgame.engine.model.Case
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.NounFunction
import com.textgame.engine.model.Person
import com.textgame.engine.model.nounphrase.Adjective
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.predicate.SentencePredicate
import com.textgame.engine.model.predicate.VerbMultipleObjects
import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.predicate.Predicates
import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.verb.Tense

/**
 * Class which "realizes" a [SimpleSentence], ie produces a final human-readable Surface Form representing that sentence.
 *
 * Since a [SimpleSentence] is only an abstract syntax structure, realizing the sentence includes simply putting the words
 * in the correct order, and introducing grammatical/linking words where appropriate.
 *
 * This also includes an element of pragmatically naming each entity referenced within the sentence. When an unknown entity
 * is referenced for the first time, it should be indefinite. Any known entity can be referred to definitely.
 *
 * Realized sentences should also use proper punctuation, which includes careful use of commas.
 */
class SentenceRealizer(

        /**
         * [NarrativeContext] for the narration in which these sentences are being used. Consumers can mutate this instance
         * to influence how [NamedEntity]s are named during sentence realization.
         */
        private val narrativeContext: NarrativeContext
) {

    /**
     * Maps [NamedEntity]s to certain [Pronouns] that should always be used when naming those entities.
     *
     * This allows sentences to be realized in first or second person for a particular entity, for example a protagonist
     * or player character.
     */
    private val personOverride: MutableMap<NamedEntity, Person> = HashMap()

    private val personPronouns: Map<Person, Pronouns> = mapOf(
            Pair(Person.SECOND, Pronouns.SECOND_PERSON_SINGULAR)
    )

    /**
     * Records the entity that was referred to most recently, per pronoun.
     */
    private val recentPronouns: MutableMap<Pronouns, NamedEntity> = HashMap()

    /**
     * Clears the record of recent [Pronouns] usage.
     *
     * This means any [NamedEntity] will be referred to at least one time by name before any of its [Pronouns] will be used
     */
    fun resetRecentPronouns() {
        recentPronouns.clear()
    }

    /**
     * Realizes a [Sentence] by producing a proper English String representation.
     *
     * This will include proper punctuation and word order, and meaningful, unambiguous names for all entities.
     */
    fun realize(sentence: Sentence): String =
            when (sentence) {
                is SimpleSentence -> realizeSimpleSentence(sentence)
                else -> throw IllegalArgumentException("Unrecognized Sentence type ${sentence.javaClass}")
            }

    private fun realizeSimpleSentence(sentence: SimpleSentence): String {
        val builder = StringBuilder()

        // Refer to the subject
        val subjectName = referToEntity(sentence.subject, sentence.subject, Case.NOMINATIVE, NounFunction.SUBJECT)
        builder.append(NounPhraseFormatter.format(subjectName, true))
                .append(" ")

        // Realize the sentence's Predicate
        builder.append(realizePredicate(sentence.predicate, sentence.subject))

        // Apply punctuation at the end of the sentence
        builder.append(".")

        return builder.toString()
    }

    private fun realizePredicate(predicate: SentencePredicate, subject: NamedEntity) =
            when (predicate) {
                is VerbPredicate -> realizeVerbPredicate(predicate, subject)
                is Predicates -> realizeVerbPredicates(predicate, subject)
                is VerbMultipleObjects -> realizeMultipleObjects(predicate, subject)
                else -> throw IllegalArgumentException("Invalid predicate type: ${predicate.javaClass}")
            }

    private fun realizeVerbPredicate(predicate: VerbPredicate, subject: NamedEntity): String {
        val builder = StringBuilder()

        // Conjugate the verb given the subject's person
        val person = personOverride[subject] ?: Person.THIRD
        val conjugatedVerb = predicate.verb.conjugate(person, Tense.SIMPLE_PRESENT)
        builder.append(conjugatedVerb)

        // Include the Direct Object (if present)
        predicate.directObject?.let {
            val directObjectName = referToEntity(predicate.directObject, subject, Case.ACCUSATIVE, NounFunction.DIRECT_OBJECT)

            builder.append(" ")
                    .append(NounPhraseFormatter.format(directObjectName))
        }

        // Include the Prepositional Phrase (if present)
        predicate.prepositionalPhrase?.let {
            val objectOfPrepositionName = referToEntity(
                    predicate.prepositionalPhrase.objectOfPreposition,
                    subject,
                    Case.ACCUSATIVE,
                    NounFunction.OBJECT_OF_PREPOSITION
            )
            builder.append(" ")
                    .append(predicate.prepositionalPhrase.preposition)
                    .append(" ")
                    .append(NounPhraseFormatter.format(objectOfPrepositionName))
        }

        return builder.toString()
    }

    private fun realizeVerbPredicates(predicate: Predicates, subject: NamedEntity): String {
        val predicateStrings = predicate.predicates.map {
            realizeVerbPredicate(it, subject)
        }

        return formatList(predicateStrings)
    }

    private fun realizeMultipleObjects(predicate: VerbMultipleObjects, subject: NamedEntity): String {
        val builder = StringBuilder()

        // Conjugate the verb given the subject's person
        val person = personOverride[subject] ?: Person.THIRD
        val conjugatedVerb = predicate.verb.conjugate(person, Tense.SIMPLE_PRESENT)
        builder.append(conjugatedVerb)
                .append(" ")

        // Include the Direct Object (if present)
        val formattedDirectObjectNames = predicate.directObjects.map {
            NounPhraseFormatter.format(referToEntity(it, subject, Case.ACCUSATIVE, NounFunction.DIRECT_OBJECT))
        }
        builder.append(formatList(formattedDirectObjectNames))

        // Include the Prepositional Phrase (if present)
        predicate.prepositionalPhrase?.let {
            val objectOfPrepositionName = referToEntity(
                    predicate.prepositionalPhrase.objectOfPreposition,
                    subject,
                    Case.ACCUSATIVE,
                    NounFunction.OBJECT_OF_PREPOSITION
            )
            builder.append(" ")
                    .append(predicate.prepositionalPhrase.preposition)
                    .append(" ")
                    .append(NounPhraseFormatter.format(objectOfPrepositionName))
        }

        return builder.toString()
    }

    /**
     * Configure the pronouns to be used when referring to the given entity.
     *
     * These pronouns will ALWAYS be used to refer to the entity, rather than its name or stated pronouns.
     * Use this typically to designate one entity as "you" or "I" throughout a narrative.
     */
    fun overridePerson(entity: NamedEntity, person: Person) {
        personOverride[entity] = person
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
     * - Whether the entity is "owned" by another entity, and should use a possessive form
     * - Grammatical context (how is this word functioning within the sentence)
     *
     * As a side-effect, the [NamedEntity] will be considered a known entity in the [NarrativeContext], which will change
     * how it is referred to in the future.
     */
    private fun referToEntity(entity: NamedEntity, subjectOfSentence: NamedEntity, case: Case, function: NounFunction): NounPhrase {
        val name: NounPhrase

        if (case == Case.ACCUSATIVE && entity == subjectOfSentence) {
            // If the Subject and Direct Object are the same, refer to the Direct Object via reflexive pronoun
            // This may be the entity's native pronoun, or one configured via [overridePerson(NamedEntity, Pronouns)]
            if (personOverride.containsKey(entity)) {
                val pronouns = personPronouns[personOverride[entity]]
                name = pronouns!!.reflexive
            } else {
                name = entity.pronouns.reflexive
            }
        } else if (recentPronouns.containsKey(entity.pronouns) && recentPronouns[entity.pronouns] == entity) {
            // If the most recent entity that was named, and that shares pronouns with this entity, IS this etnity,
            // then its pronouns can be used to unambiguously refer to it
            name = entity.pronouns.get(case)
        } else {
            name = when {
                narrativeContext.isKnownEntity(entity) && entity.isOwnedBy(subjectOfSentence) ->
                    Adjective(subjectOfSentence.pronouns.possessiveDeterminer.value, entity.name)
                personOverride.containsKey(entity) -> personPronouns[personOverride[entity]]!!.get(case)
                narrativeContext.isKnownEntity(entity) -> entity.name.head().definite()
                else -> entity.name.indefinite()
            }
        }

        // Make the entity now known in the narrative context
        narrativeContext.addKnownEntity(entity)

        /*
         Note the pronouns for this entity, but only if entity's noun function is "strong" enough to
         reserve that pronoun for later use.
         */
        if (function == NounFunction.SUBJECT || function == NounFunction.DIRECT_OBJECT)
            recentPronouns[entity.pronouns] = entity

        return name
    }
}
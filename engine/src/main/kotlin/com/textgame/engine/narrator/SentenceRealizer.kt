package com.textgame.engine.narrator

import com.textgame.engine.FormattingUtil.Companion.formatList
import com.textgame.engine.format.DefaultNounPhraseFormatter
import com.textgame.engine.format.NounPhraseFormatter
import com.textgame.engine.model.*
import com.textgame.engine.model.nounphrase.*
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
        private val narrativeContext: NarrativeContext,

        /**
         * Enables debug mode.
         *
         * If enabled, all names (including pronouns) will include a debug string to identify the referent.
         */
        private val debug: Boolean = false
) {

    /**
     * Maps [NamedEntity]s to certain [Pronouns] that should always be used when naming those entities.
     *
     * This allows sentences to be realized in first or second person for a particular entity, for example a protagonist
     * or player character.
     */
    private val personOverride: MutableMap<NamedEntity, GrammaticalPerson> = HashMap()

    /**
     * Records the entity that was referred to most recently, per pronoun.
     */
    private val pronounContext: PronounContext = PronounContext()

    /**
     * Configure the pronouns to be used when referring to the given entity.
     *
     * These pronouns will ALWAYS be used to refer to the entity, rather than its name or stated pronouns.
     * Use this typically to designate one entity as "you" or "I" throughout a narrative.
     */
    fun overridePerson(entity: NamedEntity, person: GrammaticalPerson) {
        personOverride[entity] = person

        // Make sure pronoun usage reflects the grammatical person
        pronounContext.overridePerson(entity, person)
    }

    /**
     * Clears the record of recent [Pronouns] usage.
     *
     * This means any [NamedEntity] will be referred to at least one time by name before any of its [Pronouns] will be used
     */
    fun clearPronounHistory() {
        pronounContext.clearHistory()
    }

    /**
     * Realizes a [Sentence] by producing a proper English String representation.
     *
     * This will include proper punctuation and word order, and meaningful, unambiguous names for all entities.
     */
    fun realize(sentence: Sentence): List<SentenceElement> =
            when (sentence) {
                is SimpleSentence -> realizeSimpleSentence(sentence)
                else -> throw IllegalArgumentException("Unrecognized Sentence type ${sentence.javaClass}")
            }

    private fun realizeSimpleSentence(sentence: SimpleSentence): List<SentenceElement> {
        val elements = mutableListOf<SentenceElement>()

        // Refer to the subject
        val subjectName = referToEntity(sentence.subject, sentence.subject, Case.NOMINATIVE, NounFunction.SUBJECT)
        elements.add(subjectName)

        // Realize the sentence's Predicate
        elements.addAll(realizePredicate(sentence.predicate, sentence.subject))

        // Apply punctuation at the end of the sentence
        elements.add(PunctuationElement("."))

        return elements
    }

    private fun realizePredicate(predicate: SentencePredicate, subject: NamedEntity) =
            when (predicate) {
                is VerbPredicate -> realizeVerbPredicate(predicate, subject)
                is Predicates -> realizeVerbPredicates(predicate, subject)
                is VerbMultipleObjects -> realizeMultipleObjects(predicate, subject)
                else -> throw IllegalArgumentException("Invalid predicate type: ${predicate.javaClass}")
            }

    private fun realizeVerbPredicate(predicate: VerbPredicate, subject: NamedEntity): List<SentenceElement> {
        val elements = mutableListOf<SentenceElement>()

        // Conjugate the verb given the subject's person
        val person = personOverride[subject] ?: GrammaticalPerson.THIRD
        val conjugatedVerb = predicate.verb.conjugate(person, Tense.SIMPLE_PRESENT)
        elements.add(TextElement(conjugatedVerb))

        // Include the Direct Object (if present)
        predicate.directObject?.let {
            val directObjectName = referToEntity(predicate.directObject, subject, Case.ACCUSATIVE, NounFunction.DIRECT_OBJECT)

            elements.add(directObjectName)
        }

        // Include the Prepositional Phrase (if present)
        predicate.prepositionalPhrase?.let {
            val objectOfPrepositionName = referToEntity(
                    predicate.prepositionalPhrase.objectOfPreposition,
                    subject,
                    Case.ACCUSATIVE,
                    NounFunction.OBJECT_OF_PREPOSITION
            )
            elements.add(TextElement(predicate.prepositionalPhrase.preposition))
            elements.add(objectOfPrepositionName)
        }

        return elements
    }

    private fun realizeVerbPredicates(predicate: Predicates, subject: NamedEntity): List<SentenceElement> =
        flattenElementList(predicate.predicates.map {
            realizeVerbPredicate(it, subject)
        })

    private fun realizeMultipleObjects(predicate: VerbMultipleObjects, subject: NamedEntity): List<SentenceElement> {
        val elements = mutableListOf<SentenceElement>()

        // Conjugate the verb given the subject's person
        val person = personOverride[subject] ?: GrammaticalPerson.THIRD
        val conjugatedVerb = predicate.verb.conjugate(person, Tense.SIMPLE_PRESENT)
        elements.add(TextElement(conjugatedVerb))

        // Include the Direct Object (if present)
        val directObjectNames = mutableListOf<SentenceElement>()
        directObjectNames.addAll(predicate.directObjects.map {
            referToEntity(it, subject, Case.ACCUSATIVE, NounFunction.DIRECT_OBJECT)
        })
        elements.addAll(joinElementList(directObjectNames))

        // Include the Prepositional Phrase (if present)
        predicate.prepositionalPhrase?.let {
            val objectOfPreposition = predicate.prepositionalPhrase.objectOfPreposition
            val objectOfPrepositionName = referToEntity(
                    objectOfPreposition,
                    subject,
                    Case.ACCUSATIVE,
                    NounFunction.OBJECT_OF_PREPOSITION
            )
            elements.add(TextElement(predicate.prepositionalPhrase.preposition))
            elements.add(objectOfPrepositionName)
        }

        return elements
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
    private fun referToEntity(entity: NamedEntity, subjectOfSentence: NamedEntity, case: Case, function: NounFunction): NameElement {
        val name: NounPhrase

        val pronouns = pronounContext.getPronouns(entity)

        if (case == Case.ACCUSATIVE && entity == subjectOfSentence && pronouns != null) {
            // If the Subject and Direct Object are the same, refer to the Direct Object via reflexive pronoun
            name = pronouns.reflexive
        } else if (pronounContext.shouldUsePronouns(entity)) {
            // Use a pronoun if it would be unambiguous in context
            name = pronouns!!.get(case)
        } else if (
            narrativeContext.isKnownEntity(entity) &&
                    entity.isOwnedBy(subjectOfSentence) &&
                    subjectOfSentence.pronouns != null
        ) {
            // Indicate a possessive form
            // TODO let the pronoun context decide which possessive to use
            name = Adjective(
                    if (!debug) subjectOfSentence.pronouns.possessiveDeterminer.value
                            else buildDebugPossessiveDeterminer(subjectOfSentence),
                    entity.name.head()
            )
        } else if (narrativeContext.isKnownEntity(entity)) {
            // Use a short, definite name if the entity is known
            name = entity.name.head().definite()
        } else {
            // Use a full, indefinite name for a brand new entity
            name = entity.name.indefinite()
        }

        // Track that this entity was named, so it will affect how future entities are named
        narrativeContext.addKnownEntity(entity)
        pronounContext.recordPronounUsage(entity, function)

        val nameString = DefaultNounPhraseFormatter.format(name, entity)
        return if (!debug) NameElement(name, entity)
            else NameElement(Noun("$nameString(${buildDebugName(entity)})"), entity)
    }

    /**
     * Builds a string like "her(Lydia's)" to help debug possessive determiners
     */
    private fun buildDebugPossessiveDeterminer(possessor: NamedEntity) =
        possessor.pronouns!!.possessiveDeterminer.value +
                "(${DefaultNounPhraseFormatter.format(possessor.name, possessor)}'s/${possessor.pronouns.gender})"

    /**
     * Builds a string like "bandit/MS" to help debug names
     */
    private fun buildDebugName(entity: NamedEntity): String {
        val nameString = DefaultNounPhraseFormatter.format(entity.name, entity)
        return if (entity.pronouns != null)
            "$nameString/${entity.pronouns.gender}"
        else nameString
    }

}
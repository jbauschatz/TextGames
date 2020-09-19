package com.textgame.engine.model.nounphrase

import com.textgame.engine.model.Case

/**
 * Class to represent an entity's pronouns in different parts of speech.
 *
 * An entity should be given Prounouns to reflect its grammatical person, gender, and number.
 */
data class Pronouns(

        val gender: String,

        /**
         * Form of the pronoun to be used in the Subject of a sentence.
         * Examples: "[he/she/it/they] threw the ball"
         */
        val nominative: Pronoun,

        /**
         * Form of the pronoun to be used as the Direct Object of a sentence.
         * Examples: "[he/she/it/they] threw the ball"
         */
        val accusative: Pronoun,

        /**
         * Pronouns which indicates a thing possessed by another entity
         * Examples: "That ball is [his/hers/theirs/mine]".
         *
         * This can be used as the head of a Noun Phrase to stand in for the thing being possessed.
         */
        val possessivePronoun: Pronoun,

        /**
         * Function Word used in front of a noun to express belonging
         * Examples: "Hand me [his/her/its/their/my] ball".
         *
         * This is different from the Possessive Pronouns, in that it cannot directly be substituted for the noun,
         * but only modifies it.
         */
        val possessiveDeterminer: Pronoun,

        /**
         * Form used when an entity performs an action on itself.
         * Examples: "He/she/it hurt [himself/herself/itself]
         */
        val reflexive: Pronoun
) {

    constructor(
            gender: String,
            nominative: String,
            accusative: String,
            possessivePronoun: String,
            possessiveDeterminer: String,
            reflexive: String
    ): this (
            gender,
            Pronoun(nominative),
            Pronoun(accusative),
            Pronoun(possessivePronoun),
            Pronoun(possessiveDeterminer),
            Pronoun(reflexive)
    )

    companion object {

        /**
         * Pronouns used for a grammatically masculine, singular entity
         */
        val THIRD_PERSON_SINGULAR_MASCULINE = Pronouns("MS", "he", "him", "his", "his", "himself")

        /**
         * Pronouns used for a grammatically feminine, singular entity
         */
        val THIRD_PERSON_SINGULAR_FEMININE = Pronouns("FS", "she", "her", "hers", "her", "herself")

        /**
         * Pronouns used for a grammatically neuter, singular entity
         */
        val THIRD_PERSON_SINGULAR_NEUTER = Pronouns("NS", "it", "it", "its", "its", "itself")

        /**
         * Pronouns used for an unknown or non masculine/feminine singular entity
         *
         * Note: For the reflexive form, Oxford Dictionary gives "themself", but "theirself" might sometimes feel
         * more natural
         */
        val THIRD_PERSON_SINGULAR_NON_BINARY = Pronouns("S", "they", "them", "theirs", "their", "themself")

        /**
         * Pronouns used for an unknown or grammatically neuter, plural entity.
         */
        val THIRD_PERSON_PLURAL_NEUTER = Pronouns("NPl", "they", "them", "theirs", "their", "themselves")

        /**
         * Pronouns used for a second person singular entity (direct address to one reader/listener)
         */
        val SECOND_PERSON_SINGULAR = Pronouns("S", "you", "you", "yours", "your", "yourself")

        /**
         *
         */
        val FIRST_PERSON_SINGULAR = Pronouns("S", "I", "I", "mine", "my", "myself")

    }

    fun get(case: Case) =
            when (case) {
                Case.NOMINATIVE -> nominative
                Case.ACCUSATIVE -> accusative
            }
}
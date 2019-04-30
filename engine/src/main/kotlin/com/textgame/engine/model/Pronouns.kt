package com.textgame.engine.model

/**
 * Class to represent an entity's pronouns in different parts of speech.
 *
 * An entity should be given Prounouns to reflect its grammatical person, gender, and number.
 */
data class Pronouns(

        /**
         * Form of the pronoun to be used in the Subject of a sentence.
         * Examples: "[he/she/it/they] threw the ball"
         */
        val subjective: String,

        /**
         * Form of the pronoun to be used as the Direct Object of a sentence.
         * Examples: "[he/she/it/they] threw the ball"
         */
        val objective: String,

        /**
         * Pronouns which indicates a thing possessed by another entity
         * Examples: "That ball is [his/hers/theirs/mine]".
         *
         * This can be used as the head of a Noun Phrase to stand in for the thing being possessed.
         */
        val possessivePronoun: String,

        /**
         * Function Word used in front of a noun to express belonging
         * Examples: "Hand me [his/her/its/their/my] ball".
         *
         * This is different from the Possessive Pronouns, in that it cannot directly be substituted for the noun,
         * but only modifies it.
         */
        val possessiveDeterminer: String
) {
    companion object {

        /**
         * Pronouns used for a grammatically masculine, singular entity
         */
        val THIRD_PERSON_SINGULAR_MASCULINE = Pronouns("he", "him", "his", "his")

        /**
         * Pronouns used for a grammatically feminine, singular entity
         */
        val THIRD_PERSON_SINGULAR_FEMININE = Pronouns("she", "her", "hers", "her")

        /**
         * Pronouns used for a grammatically neuter, singular entity
         */
        val THIRD_PERSON_SINGULAR_NEUTER = Pronouns("it", "it", "its", "its")

        /**
         * Pronouns used for a grammatically neuter, plural entity.
         *
         * This form also applies for non masculine/feminine singular entities.
         */
        val THIRD_PERSON_PLURAL_NEUTER = Pronouns("they", "them", "theirs", "their")
    }
}
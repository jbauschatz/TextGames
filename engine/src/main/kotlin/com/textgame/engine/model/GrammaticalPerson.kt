package com.textgame.engine.model

/**
 * Grammatical person.
 *
 * That can determine how to conjugate a verb, as well as which pronoun to use when referring to an entity.
 */
enum class GrammaticalPerson {

    /**
     * First person.
     *
     * Used for personal point of view, ie "I", "we", "us"
     */
    FIRST,

    /**
     * Second person.
     *
     * Used in direct address of a person, ie "you", "your", "yourself"
     */
    SECOND,

    /**
     * Third person.
     *
     * Used when referring indirectly to a person, ie "he", "she", "it", "that".
     */
    THIRD
}
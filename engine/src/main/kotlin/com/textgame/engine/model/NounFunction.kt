package com.textgame.engine.model

/**
 * The grammatical function of a noun within a sentence
 */
enum class NounFunction {

    /**
     * The noun is the subject of the sentence
     *
     * Example: "The *cat* saw the mouse in the hole"
     */
    SUBJECT,

    /**
     * The noun is the direct object of the sentence
     *
     * Example: "The cat saw the *mouse* in the hole"
     */
    DIRECT_OBJECT,

    /**
     * The noun is the object of a prepositional phrase
     *
     * Example: "The cat saw the mouse in the *hole*"
     */
    OBJECT_OF_PREPOSITION
}
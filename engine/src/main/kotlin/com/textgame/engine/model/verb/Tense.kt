package com.textgame.engine.model.verb

/**
 * Verb tense.
 *
 * Used to contextualize an action in time. Specifies whether the action is complete, ongoing, etc.
 */
enum class Tense {

    /**
     * The action was completed in the past.
     *
     * ie, "he ran"
     */
    SIMPLE_PAST,

    /**
     * The action is being performed at present.
     *
     * ie, "she runs"
     */
    SIMPLE_PRESENT,
}
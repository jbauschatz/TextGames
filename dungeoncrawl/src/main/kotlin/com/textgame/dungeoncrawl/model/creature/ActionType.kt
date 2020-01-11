package com.textgame.dungeoncrawl.model.creature

/**
 * Represents the cost of a [Creature]'s action in the Games "move economy".
 */
enum class ActionType {

    /**
     * Cost of taking an attack action, such as attacking a [Creature] or casting a spell
     */
    ATTACK,

    /**
     * Cost of a movement action such as moving to a different [Location]
     */
    MOVE,

    /**
     * Cost of a free action, such as equipping certain items, or player actions like inspecting inventory
     */
    FREE
}
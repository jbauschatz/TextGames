package com.textgame.engine.narrator

import com.textgame.engine.model.NamedEntity

/**
 * Tracks which [NamedEntity]
 */
class NarrativeContext {

    private val knownEntities: MutableSet<NamedEntity> = HashSet()

    fun addKnownEntity(entity: NamedEntity) =
            knownEntities.add(entity)

    fun isKnownEntity(entity: NamedEntity): Boolean =
            knownEntities.contains(entity)
}
package com.textgame.dungeoncrawl.model

import com.textgame.engine.model.NamedEntity

fun sameEntity(entityA: NamedEntity, entityB: NamedEntity): Boolean =
        entityA.id == entityB.id
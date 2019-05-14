package com.textgame.engine.model.sentence

import com.textgame.engine.model.NamedEntity

data class SimpleSentence(

        val subject: NamedEntity,

        val verb: String,

        val directObject: NamedEntity?
) {
    constructor(subject: NamedEntity, verb: String): this(subject, verb, null)
}
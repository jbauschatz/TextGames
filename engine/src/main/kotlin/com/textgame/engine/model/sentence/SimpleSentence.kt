package com.textgame.engine.model.sentence

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.predicate.SentencePredicate

data class SimpleSentence(

        val subject: NamedEntity,

        val predicate: SentencePredicate
): Sentence
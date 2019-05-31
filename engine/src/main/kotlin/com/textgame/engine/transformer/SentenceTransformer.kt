package com.textgame.engine.transformer

import com.textgame.engine.model.sentence.Sentence
import com.textgame.engine.model.sentence.SimpleSentence

interface SentenceTransformer {

    fun canTransform(vararg sentences: SimpleSentence): Boolean

    fun transform(vararg sentences: SimpleSentence): Sentence
}
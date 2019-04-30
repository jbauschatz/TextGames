package com.textgame.engine

import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import com.textgame.engine.model.sentence.SimpleSentence
import java.lang.StringBuilder

class Narrator {

    fun writeSentence(sentence: SimpleSentence): String {
        val builder = StringBuilder()
                .append(NounPhraseFormatter.format(sentence.subject.getName(), true))
                .append(" ")
                .append(sentence.verb)

        sentence.directObject?.let {
            builder.append(" ")
                    .append(NounPhraseFormatter.format(sentence.directObject.getName(), false))
        }
        builder.append(".")

        return builder.toString()
    }
}
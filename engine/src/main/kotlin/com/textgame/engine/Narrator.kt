package com.textgame.engine

import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import com.textgame.engine.model.sentence.SimpleSentence
import java.lang.StringBuilder

class Narrator {

    fun writeSentence(sentence: SimpleSentence): String {
        val subjectName = sentence.subject.getName().indefinite()

        val builder = StringBuilder()
                .append(NounPhraseFormatter.format(subjectName, true))
                .append(" ")
                .append(sentence.verb)

        sentence.directObject?.let {
            val objectName = sentence.directObject.getName().indefinite()

            builder.append(" ")
                    .append(NounPhraseFormatter.format(objectName, false))
        }
        builder.append(".")

        return builder.toString()
    }
}
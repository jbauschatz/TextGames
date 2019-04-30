package com.textgame.engine.narrator

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import com.textgame.engine.model.sentence.SimpleSentence
import java.lang.StringBuilder

class Narrator(
        val narrativeContext: NarrativeContext
) {

    fun writeSentence(sentence: SimpleSentence): String {
        val subjectName = referToByName(sentence.subject)

        val builder = StringBuilder()
                .append(NounPhraseFormatter.format(subjectName, true))
                .append(" ")
                .append(sentence.verb)

        sentence.directObject?.let {
            val objectName = referToByName(sentence.directObject)

            builder.append(" ")
                    .append(NounPhraseFormatter.format(objectName, false))
        }
        builder.append(".")

        return builder.toString()
    }

    protected fun referToByName(namedEntity: NamedEntity): NounPhrase {
        val name = if (narrativeContext.isKnownEntity(namedEntity)) namedEntity.getName().definite()
                else namedEntity.getName().indefinite()

        narrativeContext.addKnownEntity(namedEntity)
        return name
    }
}